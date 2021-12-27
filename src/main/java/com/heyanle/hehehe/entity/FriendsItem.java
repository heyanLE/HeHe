package com.heyanle.hehehe.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by HeYanLe on 2021/5/30 15:36.
 * https://github.com/heyanLE
 */
@Getter
@Setter
@Entity(name = "friends")
public class FriendsItem {

    @Id
    @GeneratedValue
    private Long id;

    @SerializedName("account_i")
    @Column(name = "account_i")
    private String accountI = "";

    @SerializedName("account_ii")
    @Column(name = "account_ii")
    private String accountII = "";

    @SerializedName("create_time")
    @Column(name = "create_time")
    private Long createTime = 0L;

}
