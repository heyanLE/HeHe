package com.heyanle.hehehe.websocket.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/6/3 15:05.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class DeleteFriendInfo {

    private String uuid ;

    @SerializedName("friend_username")
    private String friendUsername;

}
