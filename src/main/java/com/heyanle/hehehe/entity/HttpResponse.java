package com.heyanle.hehehe.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/30 20:22.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class HttpResponse <T>{

    private Integer code;
    private String msg;
    private T data = null;

    public static <T> HttpResponse<T> withoutData(int code, String msg){
        HttpResponse<T> httpResponse = new HttpResponse<>();
        httpResponse.code = code;
        httpResponse.msg = msg;
        return httpResponse;
    }

    public static <T> HttpResponse<T> withData(int code, String msg, T data){
        HttpResponse<T> httpResponse = new HttpResponse<>();
        httpResponse.code = code;
        httpResponse.msg = msg;
        httpResponse.data = data;
        return httpResponse;
    }

}
