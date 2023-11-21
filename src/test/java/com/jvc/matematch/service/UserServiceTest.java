//package com.jvc.matematch.service;
//
//// [编程学习交流圈](https://www.code-nav.cn/) 连接万名编程爱好者，一起优秀！20000+ 小伙伴交流分享、40+ 大厂嘉宾一对一答疑、100+ 各方向编程交流群、4000+ 编程问答参考
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.jvc.matematch.common.ErrorCode;
//import com.jvc.matematch.exception.BusinessException;
//import com.jvc.matematch.model.domain.User;
//import org.apache.commons.lang3.StringUtils;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * 用户服务测试
// *
// * @author <a href="https://github.com/jarvischan666">JarvisChan</a>
// * @from <a href="https://jvc.icu">编程导航知识星球</a>
// */
//@SpringBootTest
//public class UserServiceTest {
//
//    @Resource
//    private UserService userService;
//
//    @Test
//    public void testSearchUsersByTags() {
//        //create a tag list,Do not use list of
//        List<String> tagNameList = new ArrayList<>();
//        tagNameList.add("java");
//        // make a for loop to do ten times search and then calculate the average time
//        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) {
//            List<User> userList = userService.searchUsersByTags(tagNameList);
//            Assertions.assertNotNull(userList);
//        }
//        long endTime = System.currentTimeMillis();
////        List<User> userList = userService.searchUsersByTags(tagNameList);
////        System.out.println(userList);
//        System.out.println("sql average query time = " + (endTime - startTime) / 1000);
//    }
//
//
//
//    /**
//     * 测试添加用户
//     */
//    @Test
//    public void testAddUser() {
//        User user = new User();
//        user.setUsername("jvc");
//        user.setUserAccount("123");
//        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
//        user.setGender(0);
//        user.setUserPassword("xxx");
//        user.setPhone("123");
//        user.setEmail("456");
//        boolean result = userService.save(user);
//        System.out.println(user.getId());
//        Assertions.assertTrue(result);
//    }
//
//    // https://www.code-nav.cn/
//
//    /**
//     * 测试更新用户
//     */
//    @Test
//    public void testUpdateUser() {
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("dogjvc");
//        user.setUserAccount("123");
//        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
//        user.setGender(0);
//        user.setUserPassword("xxx");
//        user.setPhone("123");
//        user.setEmail("456");
//        boolean result = userService.updateById(user);
//        Assertions.assertTrue(result);
//    }
//
//    /**
//     * 测试删除用户
//     */
//    @Test
//    public void testDeleteUser() {
//        boolean result = userService.removeById(1L);
//        Assertions.assertTrue(result);
//    }
//
//    // https://space.bilibili.com/12890453/
//
//    /**
//     * 测试获取用户
//     */
//    @Test
//    public void testGetUser() {
//        User user = userService.getById(1L);
//        Assertions.assertNotNull(user);
//    }
//
//    /**
//     * 测试用户注册
//     */
//    @Test
//    void userRegister() {
//        String userAccount = "jvc";
//        String userPassword = "";
//        String checkPassword = "123456";
//        String planetCode = "1";
//        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        Assertions.assertEquals(-1, result);
//        userAccount = "yu";
//        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        Assertions.assertEquals(-1, result);
//        userAccount = "jvc";
//        userPassword = "123456";
//        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        Assertions.assertEquals(-1, result);
//        userAccount = "yu pi";
//        userPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        Assertions.assertEquals(-1, result);
//        checkPassword = "123456789";
//        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        Assertions.assertEquals(-1, result);
//        userAccount = "dogjvc";
//        checkPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        Assertions.assertEquals(-1, result);
//        userAccount = "jvc";
//        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        Assertions.assertEquals(-1, result);
//    }
//}