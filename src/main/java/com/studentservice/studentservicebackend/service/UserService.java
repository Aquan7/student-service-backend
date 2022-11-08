package com.studentservice.studentservicebackend.service;

import com.studentservice.studentservicebackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studentservice.studentservicebackend.model.request.user.UserUpdateRequest;
import com.studentservice.studentservicebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 二次确认密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 管理员登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    UserVO adminLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 管理员封号
     * @param id
     * @return
     */
    boolean userBanned(long id);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(UserVO loginUser);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    UserVO getLoginUser(HttpServletRequest request);

    /**
     * 用户更新信息
     * @param user
     * @param loginUser
     * @return
     */
    int updateUser(UserUpdateRequest user, UserVO loginUser);
}
