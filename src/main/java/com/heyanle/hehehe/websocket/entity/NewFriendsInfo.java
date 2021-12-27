package com.heyanle.hehehe.websocket.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/31 13:23.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class NewFriendsInfo {

    private String uuid;

    @SerializedName("friend_username")
    private String friendUsername;
}
