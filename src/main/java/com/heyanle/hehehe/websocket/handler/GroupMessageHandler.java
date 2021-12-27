package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.GroupMessageInfo;

/**
 * Created by HeYanLe on 2021/5/31 20:57.
 * https://github.com/heyanLE
 */
public class GroupMessageHandler implements EventHandler{

    @Override
    public String getKey() {
        return "group_message";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {
        GroupMessageInfo info1 = manager.getGson().fromJson(msg, GroupMessageInfo.class);
        manager.sendGroup(info.getUsername(), info1.getGroupId(), info1.getMsg(), info1.getTime(), info1.getUuid());
    }
}
