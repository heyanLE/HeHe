package com.heyanle.hehehe.websocket.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/6/11 16:48.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class GetMemberInfo {

    private String uuid;

    @SerializedName("group_id")
    private Long groupId;
}
