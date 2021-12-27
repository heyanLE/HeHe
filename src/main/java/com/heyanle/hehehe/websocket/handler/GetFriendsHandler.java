package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.entity.Account;
import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import com.heyanle.hehehe.websocket.entity.EventResponse;
import com.heyanle.hehehe.websocket.entity.UuidInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeYanLe on 2021/5/30 22:45.
 * https://github.com/heyanLE
 */
public class GetFriendsHandler implements EventHandler{

    @Override
    public String getKey() {
        return "get_friends";
    }

    @Override
    public void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception{
        UuidInfo get = manager.getGson().fromJson(msg, UuidInfo.class);
        List<Account> list = manager.getAccountRepository().getAllFriendByUsername(info.getUsername());
        ArrayList<Account> accounts = new ArrayList<>(list);
        manager.accountOnline(accounts);
        for(Account a : accounts){
            a.setPassword("");
        }
        EventResponse<List<Account>> resp = EventResponse.withMsg(getKey(), get.getUuid(), accounts);
        String respString = manager.getGson().toJson(resp);
        manager.sendMessage(info.getUsername(), respString, resp.getUuid());
    }
}
