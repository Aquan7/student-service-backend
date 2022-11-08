package com.studentservice.studentservicebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentservice.studentservicebackend.common.BaseResponse;
import com.studentservice.studentservicebackend.common.ErrorCode;
import com.studentservice.studentservicebackend.common.ResultUtils;
import com.studentservice.studentservicebackend.exception.BusinessException;
import com.studentservice.studentservicebackend.mapper.UserMapper;
import com.studentservice.studentservicebackend.model.domain.User;
import com.studentservice.studentservicebackend.model.request.user.UserLoginRequest;
import com.studentservice.studentservicebackend.model.request.user.UserRegisterRequest;
import com.studentservice.studentservicebackend.model.request.user.UserUpdateRequest;
import com.studentservice.studentservicebackend.model.vo.UserVO;
import com.studentservice.studentservicebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.studentservice.studentservicebackend.constant.UserConstant.USER_BANNED;
import static com.studentservice.studentservicebackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
//@CrossOrigin(origins = {"http://localhost:63342/"})
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    // 用户注册
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest==null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount=userRegisterRequest.getUserAccount();
        String userPassword=userRegisterRequest.getUserPassword();
        String checkPassword=userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    // 用户登录
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount=userLoginRequest.getUserAccount();
        String userPassword=userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    // 管理员登录
    @PostMapping("/adminLogin")
    public BaseResponse<UserVO> adminLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount=userLoginRequest.getUserAccount();
        String userPassword=userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.adminLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    // 管理员以及用户退出
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    // 管理员封号
    @PostMapping("/banned")
    public BaseResponse<Boolean> userBanned(@RequestBody long id,HttpServletRequest request){
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result=userService.userBanned(id);
        return ResultUtils.success(result);
    }

    // 管理员以及用户对用户信息修改
    @PostMapping("update")
    public BaseResponse<Integer> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request){
        // 校验参数是否为空
        if (userUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = userService.getLoginUser(request);
        if (loginUser==null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        int result = userService.updateUser(userUpdateRequest,loginUser);
        return ResultUtils.success(result);
    }

    // 管理员删除用户
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    // 展示用户自身数据供用户使用
    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser=(UserVO)userObj;
        if (currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        if (userId<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(userId);
        Integer userStatus = user.getUserStatus();
        if (userStatus==USER_BANNED){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return ResultUtils.success(userVO);
    }

    // 管理员根据用户账号查询
    @GetMapping("/search")
    public BaseResponse<User> searchUser(String userAccount,HttpServletRequest request) {
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userAccount)){
            queryWrapper.like("userAccount",userAccount);
        }
        User userOne = userService.getOne(queryWrapper);
        if (userOne==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userOne);
    }

    // 管理员分页查看用户
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageNum,long pageSize,  HttpServletRequest request){
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Page<User> page = new Page<>(pageNum,pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> resultPage = userMapper.selectPage(page,queryWrapper);
        return ResultUtils.success(resultPage);
    }


}
