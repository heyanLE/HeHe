package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.Message;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.entity.FriendMessageInfo;

/**
 * Created by HeYanLe on 2021/5/31 13:33.
 * https://github.com/heyanLE
 */
public class FriendMessageHandler implements EventHandler{

    @Override
    public String getKey() {
        return "friend_msg";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {

        FriendMessageInfo info1 = manager.getGson().fromJson(msg, FriendMessageInfo.class);
        manager.sendFriend(info.getUsername(), info1.getToUsername(), info1.getMsg(), info1.getTime(), info1.getUuid());

    }
}
