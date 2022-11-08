package com.studentservice.studentservicebackend.model.request.user;


import lombok.Data;
import java.io.Serializable;

/**
 * 用户更新请求体
 *
 * @author Aquan
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = -3236305535690740671L;

    /**
     * 用户id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别 0 - 女  1 - 男
     */
    private Byte gender;

    /**
     * 电话
     */
    private String phone;

}
