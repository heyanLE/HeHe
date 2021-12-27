package com.heyanle.hehehe.controller.info;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by HeYanLe on 2021/5/30 21:20.
 * https://github.com/heyanLE
 */
@Getter
@Setter
public class RegisterInfo {
    private String username;
    private String password;
    private String nickname;
    @SerializedName("cover_index")
    private Integer coverIndex;
}
