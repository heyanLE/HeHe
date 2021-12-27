package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.FriendsItem;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.DeleteFriendInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;

import java.util.Optional;

/**
 * Created by HeYanLe on 2021/6/3 14:54.
 * https://github.com/heyanLE
 */
public class DeleteFriendHandler implements EventHandler{

    @Override
    public String getKey() {
        return "delete_friend";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {
        DeleteFriendInfo info1 = manager.getGson().fromJson(msg, DeleteFriendInfo.class);
        String f = info.getUsername();
        String s = info1.getFriendUsername();
        Optional<FriendsItem> o1 = manager.getFriendsRepository().getFirstByAccountIAndAccountII(f,s);
        o1.ifPresent(friendsItem -> manager.getFriendsRepository().delete(friendsItem));
        Optional<FriendsItem> o2 = manager.getFriendsRepository().getFirstByAccountIAndAccountII(s, f);
        o2.ifPresent(friendsItem -> manager.getFriendsRepository().delete(friendsItem));
        EventResponse<String> resp1 = EventResponse.withMsg(getKey(), info1.getUuid(), info1.getFriendUsername());
        EventResponse<String> resp2 = EventResponse.withMsg(getKey(), info1.getUuid(), info.getUsername());
        manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp1), resp1.getUuid());
        manager.sendMessage(info1.getFriendUsername(), manager.getGson().toJson(resp2), resp2.getUuid());
    }
}
