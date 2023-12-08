//package com.jvc.matematch.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.moxi.mogublog.utils.JsonUtils;
//import com.jvc.matematch.common.ResultUtils;
//import org.apache.commons.lang3.StringUtils;
//import com.moxi.mogublog.web.global.MessageConf;
//import com.moxi.mogublog.web.global.SQLConf;
//import com.moxi.mogublog.web.global.SysConf;
//import com.jvc.matematch.model.domain.User;
//import com.jvc.matematch.service.UserService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import me.zhyd.oauth.config.AuthConfig;
//import me.zhyd.oauth.exception.AuthException;
//import me.zhyd.oauth.model.AuthCallback;
//import me.zhyd.oauth.model.AuthResponse;
//import me.zhyd.oauth.model.AuthToken;
//import me.zhyd.oauth.request.AuthGiteeRequest;
//import me.zhyd.oauth.request.AuthGithubRequest;
//import me.zhyd.oauth.request.AuthRequest;
//import me.zhyd.oauth.utils.AuthStateUtils;
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * 第三方登录认证
// */
//@RestController
//@RequestMapping("/oauth")
//@Api(value = "认证RestApi", tags = {"AuthRestApi"})
//public class AuthRestApi {
//
//    private static Logger log = LogManager.getLogger(IndexRestApi.class);
//
//    @Autowired
//    private UserService userService;
//    @Value(value = "${justAuth.clientId.gitee}")
//    private String giteeClienId;
//    @Value(value = "${justAuth.clientSecret.gitee}")
//    private String giteeClientSecret;
//    @Value(value = "${justAuth.clientId.github}")
//    private String githubClienId;
//    @Value(value = "${justAuth.clientSecret.github}")
//    private String githubClientSecret;
//    @Value(value = "${data.webSite.url}")
//    private String webSiteUrl;
//    @Value(value = "${data.web.url}")
//    private String moguWebUrl;
//    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
//    private Long userTokenSurvivalTime;
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @ApiOperation(value = "获取认证", notes = "获取认证")
//    @RequestMapping("/render")
//    public String renderAuth(String source, HttpServletResponse response) throws IOException {
//        log.info("进入render:" + source);
//        AuthRequest authRequest = getAuthRequest(source);
//        String token = AuthStateUtils.createState();
//        String authorizeUrl = authRequest.authorize(token);
//        Map<String, String> map = new HashMap<>();
//        map.put(SQLConf.URL, authorizeUrl);
//        return ResultUtil.result(SysConf.SUCCESS, map);
//    }
//
//
//    /**
//     * oauth平台中配置的授权回调地址，以本项目为例，在创建gitee授权应用时的回调地址应为：http://127.0.0.1:8603/oauth/callback/gitee
//     */
//    @RequestMapping("/callback/{source}")
//    public void login(@PathVariable("source") String source, AuthCallback callback, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
//        log.info("进入callback：" + source + " callback params：" + JSONObject.toJSONString(callback));
//        AuthRequest authRequest = getAuthRequest(source);
//        AuthResponse response = authRequest.login(callback);
//        String result = JSONObject.toJSONString(response);
//        System.out.println(JSONObject.toJSONString(response));
//
//        Map<String, Object> map = JsonUtils.jsonToMap(result);
//        Map<String, Object> data = JsonUtils.jsonToMap(JsonUtils.objectToJson(map.get(SysConf.DATA)));
//        Map<String, Object> token = JsonUtils.jsonToMap(JsonUtils.objectToJson(data.get(SysConf.TOKEN)));
//        String accessToken = token.get(SysConf.ACCESS_TOKEN).toString();
//        User user = userService.insertUserInfo(request, result);
//
//        if (user != null) {
//            //将从数据库查询的数据缓存到redis中
//            stringRedisTemplate.opsForValue().set(SysConf.USER_TOEKN + SysConf.REDIS_SEGMENTATION + accessToken, JsonUtils.objectToJson(user), userTokenSurvivalTime, TimeUnit.SECONDS);
//        }
//
//        httpServletResponse.sendRedirect(webSiteUrl + "?token=" + accessToken);
//    }
//
//    @RequestMapping("/revoke/{source}/{token}")
//    public Object revokeAuth(@PathVariable("source") String source, @PathVariable("token") String token) throws IOException {
//        AuthRequest authRequest = getAuthRequest(source);
//        return authRequest.revoke(AuthToken.builder().accessToken(token).build());
//    }
//
//    @RequestMapping("/refresh/{source}")
//    public Object refreshAuth(@PathVariable("source") String source, String token) {
//        AuthRequest authRequest = getAuthRequest(source);
//        return authRequest.refresh(AuthToken.builder().refreshToken(token).build());
//    }
//
//    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
//    @GetMapping("/verify/{accessToken}")
//    public String verifyUser(@PathVariable("accessToken") String accessToken) {
//        String userInfo = stringRedisTemplate.opsForValue().get("TOKEN:" + accessToken);
//        if (StringUtils.isEmpty(userInfo)) {
//            return ResultUtil.result(SysConf.ERROR, MessageConf.INVALID_TOKEN);
//        } else {
//            Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
//            return ResultUtil.result(SysConf.SUCCESS, map);
//        }
//    }
//
//    @ApiOperation(value = "删除accessToken", notes = "删除accessToken")
//    @RequestMapping("/delete/{accessToken}")
//    public String deleteUserAccessToken(@PathVariable("accessToken") String accessToken) {
//        stringRedisTemplate.delete(SysConf.USER_TOEKN + SysConf.REDIS_SEGMENTATION + accessToken);
//        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
//    }
//
//
//    private AuthRequest getAuthRequest(String source) {
//        AuthRequest authRequest = null;
//        switch (source) {
//            case SysConf.GITHUB:
//                authRequest = new AuthGithubRequest(AuthConfig.builder()
//                        .clientId(githubClienId)
//                        .clientSecret(githubClientSecret)
//                        .redirectUri(moguWebUrl + "/oauth/callback/github")
//                        .build());
//                break;
//            case SysConf.GITEE:
//                authRequest = new AuthGiteeRequest(AuthConfig.builder()
//                        .clientId(giteeClienId)
//                        .clientSecret(giteeClientSecret)
//                        .redirectUri(moguWebUrl + "/oauth/callback/gitee")
//                        .build());
//                break;
//            default:
//                break;
//        }
//        if (null == authRequest) {
//            throw new AuthException(MessageConf.OPERATION_FAIL);
//        }
//        return authRequest;
//    }
//}