package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.Account;
import com.heyanle.hehehe.entity.FriendsItem;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.entity.NewFriendsInfo;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/30 23:15.
 * https://github.com/heyanLE
 */
public class NewFriendHandler implements EventHandler{

    @Override
    public String getKey() {
        return "new_friend";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {
        NewFriendsInfo f = manager.getGson().fromJson(msg, NewFriendsInfo.class);

        Optional<Account> option = manager.getAccountRepository().findFirstByUsername(f.getFriendUsername());
        if(option.isEmpty()){
            EventResponse<String> resp = EventResponse.withMsg(getKey(), f.getUuid(), "account not found");
            manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp), f.getUuid());
            return;
        }
        Optional<Account> o = manager.getAccountRepository().findFirstByUsername(info.getUsername());

        FriendsItem friendsItem = new FriendsItem();
        FriendsItem friendsItem1 = new FriendsItem();
        friendsItem.setCreateTime(System.currentTimeMillis());
        friendsItem.setAccountI(info.getUsername());
        friendsItem.setAccountII(f.getFriendUsername());

        friendsItem1.setCreateTime(friendsItem.getCreateTime());
        friendsItem1.setAccountI(f.getFriendUsername());
        friendsItem1.setAccountII(info.getUsername());

        friendsItem = manager.getFriendsRepository().save(friendsItem);
        friendsItem1 = manager.getFriendsRepository().save(friendsItem1);


        EventResponse<Account> resp = EventResponse.withMsg(getKey(), f.getUuid(), option.get());
        EventResponse<Account> resp1 = EventResponse.withMsg(getKey(), f.getUuid(), o.get());

        manager.newFriend(info.getUsername(), f.getFriendUsername());
        manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp), f.getUuid());
        manager.sendMessage(f.getFriendUsername(), manager.getGson().toJson(resp1), f.getUuid());
    }
}
