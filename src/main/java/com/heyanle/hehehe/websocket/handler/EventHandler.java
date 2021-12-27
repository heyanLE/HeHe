package com.heyanle.hehehe.websocket.handler;

import com.heyanle.hehehe.websocket.WebsocketManager;
import com.heyanle.hehehe.websocket.entity.ClientInfo;

import java.util.HashMap;

/**
 * Created by HeYanLe on 2021/5/30 22:40.
 * https://github.com/heyanLE
 */
public interface EventHandler {

    default void register(HashMap<String, EventHandler> map){
        map.put(getKey(), this);
    }

    String getKey();

    void handle(WebsocketManager manager, ClientInfo info, String msg) throws Exception;

}
