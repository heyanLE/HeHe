package com.heyanle.hehehe.controller.info;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/31 21:31.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class MessageInfo {

    private String token;

    private Integer page;

    private String friend;

    @SerializedName("page_size")
    private Integer pageSize;

    private Long time;

}
