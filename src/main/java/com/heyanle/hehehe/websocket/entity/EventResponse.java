package com.heyanle.hehehe.websocket.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/30 23:02.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class EventResponse <T> {

    private String key;
    private String uuid;
    private T msg = null;

    public static <T> EventResponse<T> withoutMsg(String key, String uuid){
        EventResponse<T> resp = new EventResponse<>();
        resp.key = key;
        resp.uuid = uuid;
        return resp;
    }

    public static <T> EventResponse<T> withMsg(String key, String uuid, T msg){
        EventResponse<T> resp = new EventResponse<>();
        resp.key = key;
        resp.uuid = uuid;
        resp.msg = msg;
        return resp;
    }

}
