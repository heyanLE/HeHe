package com.heyanle.hehehe.websocket;

import com.heyanle.hehehe.helper.JWTHelper;
import com.heyanle.hehehe.websocket.entity.ClientInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import static javax.websocket.CloseReason.CloseCodes.PROTOCOL_ERROR;

/**
 * Created by HeYanLe on 2021/5/30 21:49.
 * https://github.com/heyanLE
 */
@ServerEndpoint(value = "/ws/{token}")
@Controller
public class WebSocketServer {

    private static JWTHelper jwtHelper;
    private static WebsocketManager manager;

    @Autowired
    public void setJwtHelper(JWTHelper jwtHelper) {
        WebSocketServer.jwtHelper = jwtHelper;
    }

    @Autowired
    public void setManager(WebsocketManager manager) {
        WebSocketServer.manager = manager;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        try{

            String username = jwtHelper.getUsername(token);
            if(username.isEmpty()){
                session.close(new CloseReason(PROTOCOL_ERROR, "Token error"));
            }
            ClientInfo info = new ClientInfo();
            info.setSession(session);
            info.setUsername(username);
            info.setToken(token);
            manager.newClient(info);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @OnClose
    public void onClose(Session session) {
        manager.removeClient(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        manager.newMessage(message, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

}
