package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.GroupMemberItem;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.AddGroupInfo;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;

import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/31 21:09.
 * https://github.com/heyanLE
 */
public class ExitGroupHandler implements EventHandler{

    @Override
    public String getKey() {
        return "exit_group";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {
        AddGroupInfo addGroupInfo = manager.getGson().fromJson(msg, AddGroupInfo.class);
        Optional<GroupMemberItem> o = manager.getGroupMemberRepository().findFirstByGroupIdAndMemberUsername(addGroupInfo.getGroupId(), info.getUsername());
        o.ifPresent(groupMemberItem -> manager.getGroupMemberRepository().delete(groupMemberItem));
        if(manager.getGroupMemberRepository().countAllByGroupId(addGroupInfo.getGroupId()) == 0){
            manager.getGroupRepository().deleteById(addGroupInfo.getGroupId());
        }
        manager.exitGroup(info.getUsername(),addGroupInfo.getGroupId());
        EventResponse<String> resp = EventResponse.withMsg(getKey(), addGroupInfo.getUuid(), "completely");
        manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp), addGroupInfo.getUuid());
    }
}
