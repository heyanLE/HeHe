package com.heyanle.hehehe.websocket.handler;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.heyanle.hehehe.entity.Group;
import com.heyanle.hehehe.entity.GroupMemberItem;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.entity.AddGroupInfo;

import java.util.Optional;

/**
 * Created by HeYanLe on 2021/5/30 23:17.
 * https://github.com/heyanLE
 */
public class AddGroupHandler implements EventHandler{

    @Override
    public String getKey() {
        return "add_group";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {

        AddGroupInfo addGroupInfo = manager.getGson().fromJson(msg, AddGroupInfo.class);

        Optional<Group> o = manager.getGroupRepository().findById(addGroupInfo.getGroupId());
        if(o.isEmpty()){
            EventResponse<String> resp = EventResponse.withMsg(getKey(), addGroupInfo.getUuid(), "group not found");
            manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp), addGroupInfo.getUuid());
            return;
        }

        Long id = addGroupInfo.getGroupId();
        String username = info.getUsername();
        GroupMemberItem item = new GroupMemberItem();
        item.setGroupId(id);
        item.setMemberUsername(username);
        item.setIdentity(GroupMemberItem.IDENTITY_MEMBER);
        item = manager.getGroupMemberRepository().save(item);
        manager.addGroup(username, id);
        EventResponse<Group> resp = EventResponse.withMsg(getKey(), addGroupInfo.getUuid(), o.get());
        manager.sendMessage(username, manager.getGson().toJson(resp), addGroupInfo.getUuid());
    }
}
