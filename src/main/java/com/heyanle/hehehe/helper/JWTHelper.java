package com.heyanle.hehehe.helper;

import org.springframework.stereotype.Service;

/**
 * Created by HeYanLe on 2021/5/30 20:49.
 * https://github.com/heyanLE
 */
public interface JWTHelper {

    public static long EXPIRATION_DATA = 1000*60*60*24*30L;

    String sign(String username, Long time);

    Boolean verity(String token, String username);

    String getUsername(String token);

}
