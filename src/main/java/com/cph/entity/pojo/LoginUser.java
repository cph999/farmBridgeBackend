package com.cph.entity.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginUser {

    private String phone;
    private String verificationCode;
}