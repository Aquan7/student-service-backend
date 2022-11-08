package com.studentservice.studentservicebackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author Aquan
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -674281535335636557L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
