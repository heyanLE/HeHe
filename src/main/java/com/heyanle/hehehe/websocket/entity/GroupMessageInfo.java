package com.heyanle.hehehe.websocket.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/31 20:57.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class GroupMessageInfo {

    private String uuid;

    @SerializedName("group_id")
    private Long groupId;

    private String msg;

    private Long time;
}
