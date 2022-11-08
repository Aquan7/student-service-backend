package com.studentservice.studentservicebackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author Aquan
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -674281535335636557L;

    private String userAccount;

    private String userPassword;

}
