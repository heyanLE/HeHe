package com.heyanle.hehehe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by HeYanLe on 2021/5/30 15:36.
 * https://github.com/heyanLE
 */
@Getter
@Setter
@Entity(name = "account")
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @GeneratedValue
    private String username = "";

    @Expose()
    private String password = "";

    @Transient
    @SerializedName("is_online")
    private Boolean isOnline = false;

    @Transient
    private String token = "";

    @SerializedName("create_time")
    @Column(name = "create_time")
    private Long createTime = 0L;




}
