package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.Account;
import com.heyanle.hehehe.entity.Group;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.entity.UuidInfo;

import java.util.List;

/**
 * Created by HeYanLe on 2021/5/30 23:11.
 * https://github.com/heyanLE
 */
public class GetGroupHandler implements EventHandler{

    @Override
    public String getKey() {
        return "get_group";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {
        UuidInfo get = manager.getGson().fromJson(msg, UuidInfo.class);
        List<Group> list = manager.getGroupRepository().getAllGroupByMemberUsername(info.getUsername());
        EventResponse<List<Group>> resp = EventResponse.withMsg(getKey(), get.getUuid(), list);
        String respString = manager.getGson().toJson(resp);
        manager.sendMessage(info.getUsername(), respString, resp.getUuid());
    }
}
