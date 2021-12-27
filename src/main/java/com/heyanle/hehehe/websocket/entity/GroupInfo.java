package com.heyanle.hehehe.websocket.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/31 21:04.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class GroupInfo {

    private String uuid;

    @SerializedName("group_name")
    private String groupName;

}
