package com.heyanle.hehehe.websocket.entity;

import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;

/**
 * Created by HeYanLe on 2021/5/30 22:28.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class ClientInfo {

    private Session session;
    private String username;
    private String token;

}
