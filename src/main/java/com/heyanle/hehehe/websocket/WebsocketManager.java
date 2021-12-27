package com.heyanle.hehehe.websocket;

import com.google.gson.Gson;
import com.heyanle.hehehe.entity.*;
import com.heyanle.hehehe.repository.*;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.handler.*;
import com.mysql.cj.xdevapi.Client;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by HeYanLe on 2021/5/30 22:04.
 * https://github.com/heyanLE
 */
@Service
public class WebsocketManager {

    @Getter
    private final Gson gson = new Gson();
    @Getter
    private final AccountRepository accountRepository;
    @Getter
    private final FriendsRepository friendsRepository;
    @Getter
    private final GroupMemberRepository groupMemberRepository;
    @Getter
    private final GroupMessageRepository groupMessageRepository;
    @Getter
    private final GroupRepository groupRepository;
    @Getter
    private final MessageRepository messageRepository;

    private final Map<String, EventHandler> handlerMap;

    public WebsocketManager(AccountRepository accountRepository, FriendsRepository friendsRepository, GroupMemberRepository groupMemberRepository, GroupMessageRepository groupMessageRepository, GroupRepository groupRepository, MessageRepository messageRepository) {
        this.accountRepository = accountRepository;
        this.friendsRepository = friendsRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.groupRepository = groupRepository;
        this.messageRepository = messageRepository;

        HashMap<String, EventHandler> handlerHashMap = new HashMap<>();
        new AddGroupHandler().register(handlerHashMap);
        new ExitGroupHandler().register(handlerHashMap);
        new GetFriendsHandler().register(handlerHashMap);
        new GetGroupHandler().register(handlerHashMap);
        new NewFriendHandler().register(handlerHashMap);
        new NewGroupHandler().register(handlerHashMap);
        new FriendMessageHandler().register(handlerHashMap);
        new GroupMessageHandler().register(handlerHashMap);
        new DeleteFriendHandler().register(handlerHashMap);
        new GetGroupMemberHandler().register(handlerHashMap);
        handlerMap = Collections.unmodifiableMap(handlerHashMap);
    }

    final ReentrantReadWriteLock  lock = new ReentrantReadWriteLock();

    private final HashMap<Long, Group> group = new HashMap<>();
    private final HashMap<String, ClientInfo> usernameClient = new HashMap<>();
    private final HashMap<String, String> sessionIdUsername = new HashMap<>();
    private final HashMap<Long, HashSet<String>> groupMember = new HashMap<>();
    private final HashMap<String, HashSet<Long>> onlineMemberGroupId = new HashMap<>();
    private final HashMap<String, HashSet<String>> onlineFriend = new HashMap<>();

    private final HashMap<String,HashSet<Message>> messageBuff = new HashMap<>();
    private final HashMap<String, HashSet<GroupMessage>> groupMessageBuff = new HashMap<>();

    public void newClient(ClientInfo info){

        lock.writeLock().lock();
        List<Group> groupList = groupRepository.getAllGroupByMemberUsername(info.getUsername());
        if(onlineMemberGroupId.containsKey(info.getUsername())){
            onlineMemberGroupId.get(info.getUsername()).clear();
        }else{
            onlineMemberGroupId.put(info.getUsername(), new HashSet<>());
        }
        for(Group group: groupList){
            onlineMemberGroupId.get(info.getUsername()).add(group.getId());
        }
        usernameClient.put(info.getUsername(), info);
        sessionIdUsername.put(info.getSession().getId(), info.getUsername());

        List<Account> accounts = accountRepository.getAllFriendByUsername(info.getUsername());

        EventResponse<String> accountEventResponse = EventResponse.withMsg("friend_online", ""
                +System.currentTimeMillis()+info.getUsername(), info.getUsername());
        HashSet<String> on = new HashSet<>();
        ArrayList<String> usernameList = new ArrayList<>();
        for(Account a: accounts){
            if(usernameClient.containsKey(a.getUsername())){
                a.setIsOnline(true);
                a.setPassword("");

                usernameList.add(a.getUsername());
                on.add(a.getUsername());
                if(!onlineFriend.containsKey(a.getUsername())){
                    onlineFriend.put(a.getUsername(), new HashSet<>());
                }
                onlineFriend.get(a.getUsername()).add(info.getUsername());
            }
        }
        for (String s : usernameList) {
            sendMessage(s, gson.toJson(accountEventResponse), accountEventResponse.getUuid());
        }
        onlineFriend.put(info.getUsername(), on);

        if(messageBuff.containsKey(info.getUsername())){
            for(Message msg: messageBuff.get(info.getUsername())){

                EventResponse<Message> eventResponse = EventResponse.withMsg("friend_msg",
                        System.currentTimeMillis()+"", msg);
                String resp = gson.toJson(eventResponse);
                sendMessage(info.getUsername(), resp, eventResponse.getUuid());
            }
            messageBuff.remove(info.getUsername());
        }
        if(groupMessageBuff.containsKey(info.getUsername())){
            for(GroupMessage gm : groupMessageBuff.get(info.getUsername())){
                EventResponse<GroupMessage> eventResponse = EventResponse.withMsg("group_message",
                        System.currentTimeMillis()+"", gm);
                String resp = gson.toJson(eventResponse);
                sendMessage(info.getUsername(), resp, eventResponse.getUuid());
            }
            groupMessageBuff.remove(info.getUsername());
        }


        lock.writeLock().unlock();
    }

    public void removeClient(Session session){
        lock.writeLock().lock();
        String username = sessionIdUsername.get(session.getId());
        HashSet<Long> groupIds = onlineMemberGroupId.get(username);
        onlineMemberGroupId.remove(username);
        usernameClient.remove(sessionIdUsername.get(session.getId()));
        sessionIdUsername.remove(session.getId());
        HashSet<String> friend = onlineFriend.get(username);
        if(friend != null){
            EventResponse<String> accountEventResponse = EventResponse.withMsg("friend_offline", ""+System.currentTimeMillis()+username, username);
            for(String u : friend){
                if(onlineFriend.containsKey(u)){

                    sendMessage(u, gson.toJson(accountEventResponse), accountEventResponse.getUuid());
                    onlineFriend.get(u).remove(username);
                }
            }
        }
        onlineFriend.remove(username);
        lock.writeLock().unlock();
    }

    public boolean isOnline(String username){
        lock.readLock().lock();
        boolean ans = usernameClient.containsKey(username);
        lock.readLock().unlock();
        return ans;
    }

    public void addGroup(String username, Long groupId){

        lock.writeLock().lock();

        if(onlineMemberGroupId.containsKey(username)){
            onlineMemberGroupId.get(username).add(groupId);
        }else{
            HashSet<Long> hashSet = new HashSet<>();
            hashSet.add(groupId);
            onlineMemberGroupId.put(username, hashSet);
        }
        loadGroup();
        lock.writeLock().unlock();
    }


    public void exitGroup(String username, Long groupId){
        lock.writeLock().lock();

        if(onlineMemberGroupId.containsKey(username)){
            onlineMemberGroupId.get(username).remove(groupId);
        }
        loadGroup();
        lock.writeLock().unlock();
    }

    public void loadGroup(){
        lock.writeLock().lock();
        groupMember.clear();
        this.group.clear();
        List<Group> groupList = groupRepository.findAllBy();
        for(Group group: groupList){
            groupMember.put(group.getId(), new HashSet<>());
            this.group.put(group.getId(), group);
        }
        List<GroupMemberItem> gm = groupMemberRepository.findAllBy();
        for(GroupMemberItem gmi: gm){
            groupMember.get(gmi.getGroupId()).add(gmi.getMemberUsername());
        }

        lock.writeLock().unlock();
    }

    public void sendFriend(String from, String to, String msg, Long time,String uuid){
        lock.readLock().lock();
        Message message = new Message();
        message.setTime(time);
        message.setFromUsername(from);
        message.setToUsername(to);
        message.setMsg(msg);
        message.setTime(time);
        message = messageRepository.save(message);
        EventResponse<Message> eventResponse = EventResponse.withMsg("friend_msg", uuid, message);
        String resp = gson.toJson(eventResponse);

        if(onlineFriend.containsKey(from)){
            try{
                if(usernameClient.containsKey(from)){
                    usernameClient.get(from).getSession().getBasicRemote().sendText(resp);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            if(!messageBuff.containsKey(from)){
                messageBuff.put(from, new HashSet<>());
            }
            messageBuff.get(from).add(message);
        }

        if(onlineFriend.get(from).contains(to)){

            try{
                if(usernameClient.containsKey(to)){
                    usernameClient.get(to).getSession().getBasicRemote().sendText(resp);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            if(!messageBuff.containsKey(to)){
                messageBuff.put(to, new HashSet<>());
            }
            messageBuff.get(to).add(message);
        }
        lock.readLock().unlock();
    }

    public void sendGroup(String from, Long groupId, String msg, Long time, String uuid){

        lock.readLock().lock();
        if(!groupMember.containsKey(groupId)){
            lock.readLock().unlock();
            loadGroup();
            lock.readLock().lock();
        }


        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroupId(groupId);
        groupMessage.setAccountUsername(from);
        groupMessage.setTime(time);
        groupMessage.setMsg(msg);
        groupMessage.setGroupName(group.get(groupId).getName());
        groupMessage = groupMessageRepository.save(groupMessage);

        EventResponse<GroupMessage> eventResponse = EventResponse.withMsg("group_message", uuid, groupMessage);
        String resp = gson.toJson(eventResponse);
        for(String username: groupMember.get(groupId)){
            //sendMessage(username, resp, eventResponse.getUuid());
            try{
                if(usernameClient.containsKey(username)){
                    usernameClient.get(username).getSession().getBasicRemote().sendText(resp);
                }else{
                    if(!groupMessageBuff.containsKey(username)){
                        groupMessageBuff.put(username, new HashSet<>());
                    }
                    groupMessageBuff.get(username).add(groupMessage);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        lock.readLock().unlock();
    }

    public void newFriend(String username, String friend){
        lock.readLock().lock();
        if(usernameClient.containsKey(username) && usernameClient.containsKey(friend)){
            if(onlineFriend.containsKey(username)){
                onlineFriend.get(username).add(friend);
            }else{
                HashSet<String> hashSet = new HashSet<>();
                hashSet.add(friend);
                onlineFriend.put(username, hashSet);
            }

            if(onlineFriend.containsKey(friend)){
                onlineFriend.get(friend).add(username);
            }else{
                HashSet<String> hashSet = new HashSet<>();
                hashSet.add(username);
                onlineFriend.put(friend, hashSet);
            }
        }
        lock.readLock().unlock();
    }

    public void accountOnline(ArrayList<Account> list){
        lock.readLock().lock();
        for(Account a: list){
            a.setIsOnline(usernameClient.containsKey(a.getUsername()));
        }
        lock.readLock().unlock();
    }

    public void newMessage(String msg, Session session){
        lock.readLock().lock();


        String username = sessionIdUsername.get(session.getId());
        System.out.println(username+":" + msg);
        ClientInfo info = null;
        if(username != null){
            info = usernameClient.get(username);
        }
        lock.readLock().unlock();
        String[] sp = msg.split(":");
        int id = msg.indexOf(":");
        String r = msg.substring(id+1);
        String key = sp[0];
        try{
            handlerMap.get(key).handle(this, info, r);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendMessage(String username, String message, String uuid){
        lock.readLock().lock();
        ClientInfo clientInfo = usernameClient.get(username);
        lock.readLock().unlock();
        if(clientInfo == null){
            return;
        }
        try {
            clientInfo.getSession().getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
