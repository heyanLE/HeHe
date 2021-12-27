package com.heyanle.hehehe.websocket.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/31 13:34.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class FriendMessageInfo {

    private String uuid;

    @SerializedName("to_username")
    private String toUsername;

    private String msg;

    private Long time;

}
