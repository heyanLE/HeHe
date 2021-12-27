package com.heyanle.hehehe.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by HeYanLe on 2021/5/30 15:37.
 * https://github.com/heyanLE
 */
@Getter
@Setter
@Entity(name = "group_message")
public class GroupMessage {

    @Id
    @GeneratedValue
    private Long id;

    @SerializedName("group_id")
    @Column(name = "group_id")
    private Long groupId;

    @Transient
    @SerializedName("group_name")
    private String groupName;

    @SerializedName("account_username")
    @Column(name = "account_username")
    private String accountUsername = "";

    private String msg = "";

    private Long time = 0L;

}
