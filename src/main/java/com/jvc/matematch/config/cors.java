package com.jvc.matematch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 解决CROS cookie跨域问题
@Configuration
public class cors {
    @Bean// 标记由spring创建并管理
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") //适用于所有端点，如“如/api/users、/api/products等”
                        .allowedHeaders("*") // 允许所有标头
                        .allowedMethods("*") // 允许 CORS 请求中的所有 HTTP 方法（GET、POST、PUT、DELETE 等）。
                        .allowedOriginPatterns("*") // 对任何域或 IP 的 CORS 请求的允许来源。
                        .allowCredentials(true) //允许发送cookie
                        .exposedHeaders("Authorization"); // 允许获取的额外字段
            }
        };
    }
}
