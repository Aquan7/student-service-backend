package com.studentservice.studentservicebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studentservice.studentservicebackend.common.ErrorCode;
import com.studentservice.studentservicebackend.exception.BusinessException;
import com.studentservice.studentservicebackend.model.domain.User;
import com.studentservice.studentservicebackend.model.request.user.UserUpdateRequest;
import com.studentservice.studentservicebackend.model.vo.UserVO;
import com.studentservice.studentservicebackend.service.UserService;
import com.studentservice.studentservicebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.studentservice.studentservicebackend.constant.UserConstant.*;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userAccount.length()>10){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过长");
        }
        if (userAccount.length()<userAccount.getBytes().length){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号不能含中文");
        }
        if (userPassword.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (userPassword.length()>8||checkPassword.length()>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }
        // 密码和校验密码不相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不相同");
        }

        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 2.加密
        String encryptPassword= DigestUtils.md5DigestAsHex((userPassword).getBytes());
        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(USERNAME);
        user.setAvatarUrl(DEFAULT_USER_URL);
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败");
        }
        return user.getId();
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验用户是否存在
        User user = getUser(userAccount, userPassword);

        // 2.用户脱敏
        UserVO safetyUser = new UserVO();
        BeanUtils.copyProperties(user,safetyUser);
        // 3.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }

    @Override
    public UserVO adminLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验用户是否存在
        User user = getUser(userAccount, userPassword);
        Integer userRole = user.getUserRole();
        // 2.校验用户是否是管理员
        if (userRole!=ADMIN_ROLE){
            throw new BusinessException(ErrorCode.NO_AUTH,"非管理员");
        }
        // 3.管理员脱敏
        UserVO safetyUser = new UserVO();
        BeanUtils.copyProperties(user,safetyUser);
        // 3.记录管理员的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public boolean userBanned(long id) {
        User user = getById(id);
        user.setUserStatus(USER_BANNED);
        return this.updateById(user);
    }

    private User getUser(String userAccount, String userPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过长");
        }
        if (userAccount.length()>10){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过长");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        if (userPassword.length()>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user==null){
            log.info("user login failed, userAccount Cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        if (user.getUserStatus()==USER_BANNED){
            throw new BusinessException(ErrorCode.NO_AUTH,"封禁账号禁止进入");
        }
        return user;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request){
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO userVO = (UserVO) userObj;
        return userVO != null && userVO.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(UserVO loginUser){
        // 仅管理员可查询
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User user = new User();
        BeanUtils.copyProperties(userObj,user);
        return (UserVO) userObj;
    }

    @Override
    public int updateUser(UserUpdateRequest userUpdateRequest, UserVO loginUser) {
        long userId = userUpdateRequest.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        if (!isAdmin(loginUser)&&userId!=loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userName = userUpdateRequest.getUserName();
        if (userName.length() < 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户昵称过短");
        }
        if (userName.length()>12){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户昵称过长");
        }
        String userPassword = userUpdateRequest.getUserPassword();
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        if (userPassword.length()>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过长");
        }
        String phone = userUpdateRequest.getPhone();
        if (StringUtils.isNotBlank(phone)){
            String validPattern = "^((13[0-9])|(14[5-8])|(15([0-3]|[5-9]))|(16[6])|(17[0|4|6|7|8])|(18[0-9])|(19[8-9]))\\d{8}$";
            Matcher matcher = Pattern.compile(validPattern).matcher(phone);
            boolean result = matcher.find();
            if (!result){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法手机号");
            }
        }

        // 加密
        String encryptPassword= DigestUtils.md5DigestAsHex((userPassword).getBytes());
        Byte gender = userUpdateRequest.getGender();
        if (gender!=USER_SEX_MAN){
            if (gender!=USER_SEX_WOMAN){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        User updateUser= new User();
        userUpdateRequest.setUserPassword(encryptPassword);
        BeanUtils.copyProperties(userUpdateRequest,updateUser);
        return userMapper.updateById(updateUser);
    }


}




