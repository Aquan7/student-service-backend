package com.studentservice.studentservicebackend.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户包装类（脱敏）
 *
 * @author Aquan
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

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

    /**
     * 用户角色    0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 状态 0 - 正常 1 - 封号
     */
    private Integer userStatus;

    /**
     *  是否删除   0 - 否 1 - 是
     */
    private Byte isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}