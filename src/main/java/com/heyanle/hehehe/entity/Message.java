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
 * Created by HeYanLe on 2021/5/30 15:37.
 * https://github.com/heyanLE
 */
@Getter
@Setter
@Entity(name = "message")
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @SerializedName("from_username")
    @Column(name = "from_username")
    private String fromUsername = "";

    @SerializedName("to_username")
    @Column(name = "to_username")
    private String toUsername = "";

    private String msg = "";

    private Long time = 0L;

}
