package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.Account;
import com.heyanle.hehehe.entity.GroupMemberItem;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.entity.GetMemberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by HeYanLe on 2021/6/11 16:47.
 * https://github.com/heyanLE
 */
public class GetGroupMemberHandler implements EventHandler {

    @Override
    public String getKey() {
        return "get_member";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception {
        GetMemberInfo info1 = manager.getGson().fromJson(msg, GetMemberInfo.class);
        Optional<GroupMemberItem> o = manager.getGroupMemberRepository().findFirstByGroupIdAndMemberUsername(info1.getGroupId(), info.getUsername());
        if(o.isEmpty()){
            EventResponse<String> resp = EventResponse.withMsg(getKey(), info1.getUuid(), "you are not member!");
            manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp), info1.getUuid());
            return;
        }
        List<Account> list = manager.getAccountRepository().getAllInGroup(info1.getGroupId());
        ArrayList<Account> li = new ArrayList<>(list);
        manager.accountOnline(li);
        EventResponse<List<Account>> resp = EventResponse.withMsg(getKey(), info1.getUuid(), li);
        manager.sendMessage(info.getUsername(), manager.getGson().toJson(resp), info1.getUuid());
    }
}
