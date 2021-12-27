package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.Group;
import com.heyanle.hehehe.entity.GroupMemberItem;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.entity.GroupInfo;

/**
 * Created by HeYanLe on 2021/5/30 23:16.
 * https://github.com/heyanLE
 */
public class NewGroupHandler implements EventHandler{

    @Override
    public String getKey() {
        return "new_group";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {
        GroupInfo groupInfo = manager.getGson().fromJson(msg, GroupInfo.class);
        Group group = new Group();
        group.setName(groupInfo.getGroupName());
        group.setCreateTime(System.currentTimeMillis());

        group = manager.getGroupRepository().save(group);
        GroupMemberItem item = new GroupMemberItem();
        item.setIdentity(GroupMemberItem.IDENTITY_MANAGER);
        item.setGroupId(group.getId());
        item.setMemberUsername(info.getUsername());
        item = manager.getGroupMemberRepository().save(item);

        EventResponse<Group> resp = EventResponse.withMsg(getKey(), groupInfo.getUuid(), group);
        manager.addGroup(info.getUsername(), item.getGroupId());
        manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp), resp.getUuid());
    }
}
