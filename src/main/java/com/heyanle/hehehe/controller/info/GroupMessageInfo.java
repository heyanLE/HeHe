package com.heyanle.hehehe.controller.info;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/31 21:32.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class GroupMessageInfo {
    private String token;

    @SerializedName("group_id")
    private Long groupId;

    private Integer page;

    @SerializedName("page_size")
    private Integer pageSize;

    private Long time;
}
