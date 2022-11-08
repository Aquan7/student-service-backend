package com.studentservice.studentservicebackend.constant;

import java.util.UUID;

/**
 * 用户常量
 *
 * @author Aquan
 */
public interface UserConstant {

    /**
     * 用户登录状态键
     */
    String USER_LOGIN_STATE="userLoginState";

    // --------- 权限 ----------
    /**
     * 默认用户权限
     */
    int DEFAULT_ROLE=0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE=1;

    /**
     * 男
     */
    int USER_SEX_MAN=1;

    /**
     * 女
     */
    int USER_SEX_WOMAN=0;

    /**
     * 封号
     */
    int USER_BANNED=1;

    /**
     * 默认用户名
     */
    String USERNAME="User"+ UUID.randomUUID().toString().substring(0,6);

    /**
     * 默认用户头像
     */
    String DEFAULT_USER_URL="https://xingqiu-tuchuang-1256524210.cos.ap-shanghai.myqcloud.com/5827/defaultUser.jpg";

}
