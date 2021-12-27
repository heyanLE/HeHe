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
@Entity(name = "group_member")
public class GroupMemberItem {

    public static final int IDENTITY_MEMBER = 1;
    public static final int IDENTITY_MANAGER = 2;
    public static final int IDENTITY_GROUP_CREATOR = 3;

    @Id
    @GeneratedValue
    private Long id;

    @SerializedName("group_id")
    @Column(name = "group_id")
    private Long groupId;

    @SerializedName("member_username")
    @Column(name = "member_username")
    private String memberUsername = "";

    private Integer identity = 1;
}
