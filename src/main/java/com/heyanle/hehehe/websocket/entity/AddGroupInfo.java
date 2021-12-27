package com.heyanle.hehehe.websocket.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/31 12:01.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class AddGroupInfo {

    private String uuid;

    @SerializedName("group_id")
    private Long groupId;
}
