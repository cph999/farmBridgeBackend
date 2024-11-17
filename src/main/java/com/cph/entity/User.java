package com.cph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class User {
    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;

    private String username;
    private String password;

    private String email;
    private String nickname;
    private String cover;
    private String token;
    private Date lastLoginTime;
    private String phone;
    private String address;

}