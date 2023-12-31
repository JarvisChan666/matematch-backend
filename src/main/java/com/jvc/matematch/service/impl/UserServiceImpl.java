package com.jvc.matematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.reflect.TypeToken;
import com.jvc.matematch.common.ErrorCode;
import com.jvc.matematch.contant.UserConstant;
import com.jvc.matematch.exception.BusinessException;
import com.jvc.matematch.mapper.UserMapper;
import com.jvc.matematch.service.UserService;
import com.jvc.matematch.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import com.google.gson.Gson;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jvc.matematch.contant.UserConstant.ADMIN_ROLE;
import static com.jvc.matematch.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author <a href="https://github.com/jarvischan666">JarvisChan</a>
 * @from <a href="https://jvc.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    // https://www.code-nav.cn/

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "jvc";

    /**
     * 根据标签搜搜用户
     */
    /**
     * 根据标签搜索用户。(内存过滤版)
     *
     * @param tagNameList 用户要搜索的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.判断内存中是否包含要求的标签 parallelStream()
        return userList.stream().filter(user -> {
            String tagstr = user.getTags();
//            if (StringUtils.isBlank(tagstr)){
//                return false;
//            }
            Set<String> tempTagNameSet = gson.fromJson(tagstr, new TypeToken<Set<String>>() {
            }.getType());
            //java8  Optional 来判断空
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());

            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

//    @Override
//    public Page<User> searchUsersByTags(List<String> tagNameList, Page<User> page) {
//        // 创建查询条件
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("tags", tagNameList);
//        // 执行分页查询
//        return userMapper.selectPage(page, queryWrapper);
//    }

//    private static final Gson GSON = new Gson();
//
//    @Override
//    public List<User> searchUsersByTags(List<String> tagNameList) {
//        if (CollectionUtils.isEmpty(tagNameList)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//
//        // 1. Query users whose tags contain all specified tags
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("tags", tagNameList.toString());
//        List<User> userList = userMapper.selectList(queryWrapper);
//
//        // 2. Filter users in memory
//        return userList.parallelStream().filter(user -> {
//            String tagstr = user.getTags();
//            Set<String> tempTagNameSet = GSON.fromJson(tagstr, new TypeToken<Set<String>>() {}.getType());
//            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(Collections.emptySet());
//
//            return tagNameList.stream().allMatch(tempTagNameSet::contains);
//        }).map(this::getSafetyUser).collect(Collectors.toList());
//    }

//    private static final Gson GSON = new Gson();
//    private static final RedisProperties.Jedis REDIS_CLIENT = new RedisProperties.Jedis();

//    @Override
//    public List<User> searchUsersByTags(List<String> tagNameList) {
//        if (CollectionUtils.isEmpty(tagNameList)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//
//        // Check if the result is in Redis
//        String redisKey = String.join(",", tagNameList);
//        String cachedResult = REDIS_CLIENT.set(redisKey);
//        if (cachedResult != null) {
//            return GSON.fromJson(cachedResult, new TypeToken<List<User>>() {}.getType());
//        }
//
//        // Query and filter users
//        // 1. Query users whose tags contain all specified tags
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("tags", tagNameList.toString());
//        List<User> userList = userMapper.selectList(queryWrapper);
//
//
//        // Cache the result in Redis
//        REDIS_CLIENT.set(redisKey, GSON.toJson(result));
//
//        return result;
//    }

    /**
     * 根据标签搜索用户。(sql查询版)
     *
     * @param tagNameList 用户要搜索的标签
     * @return
     * @Deprecated 过时
     */
    @Deprecated
    private List<User> searchUsersByTagBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接tag
        // like '%Java%' and like '%Python%'
        for (String tagList : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagList);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    // [加入星球](https://www.code-nav.cn/) 从 0 到 1 项目实战，经验拉满！10+ 原创项目手把手教程、7 日项目提升训练营、60+ 编程经验分享直播、1000+ 项目经验笔记

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验 如果用户没有任何要更新的值，直接报错不返回
        if (StringUtils.isAllBlank(user.getUsername(), user.getAvatarUrl(), user.getPhone(), user.getEmail(), user.getTags())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  如果及不是管理员，又不是用户自己，不能更改
        if (!isAdmin(loginUser) && loginUser.getId() != userId) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User userold = userMapper.selectById(userId);
        // 如果用户不存在
        if (userold == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // mapper提供的方法
        return userMapper.updateById(user);
    }

    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        //  请求中获取登录态
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        // get方法都是自带的
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }
}


// [加入我们](https://jvc.icu) 从 0 到 1 项目实战，经验拉满！10+ 原创项目手把手教程、7 日项目提升训练营、1000+ 项目经验笔记、60+ 编程经验分享直播