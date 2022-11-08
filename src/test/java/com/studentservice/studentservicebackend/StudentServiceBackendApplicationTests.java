package com.studentservice.studentservicebackend;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studentservice.studentservicebackend.model.domain.User;
import com.studentservice.studentservicebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@Slf4j
class StudentServiceBackendApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void testAddUser(){
        User user = new User();
        user.setId(0L);
        user.setUserName("");
        user.setUserAccount("");
        user.setUserPassword("");
        user.setAvatarUrl("");
        user.setGender((byte)0);
        user.setPhone("");
        user.setUserRole(0);
        user.setUserStatus(0);
        user.setIsDelete((byte)0);
        userService.save(user);
    }

    @Test
    void testUpdateUser(){
        User user = new User();
        user.setId(1L);
        user.setUserName("test5");
        userService.updateById(user);
    }

    @Test
    void testPhone(){
        String s="^((13[0-9])|(14[5-8])|(15([0-3]|[5-9]))|(16[6])|(17[0|4|6|7|8])|(18[0-9])|(19[8-9]))\\d{8}$";
        String phone="18138708784";
        Matcher matcher = Pattern.compile(s).matcher(phone);
        if (matcher.find()){
            log.info("成功找到");
        }
    }

    @Test
    void testUser(){
        Page<User> page = new Page<>(1,5);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> resultPage = userService.page(page, queryWrapper);
        System.out.println(resultPage);
    }

}
