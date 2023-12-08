package com.jvc.matematch;

// [加入编程导航](https://www.code-nav.cn/) 深耕编程提升【两年半】、国内净值【最高】的编程社群、用心服务【20000+】求学者、帮你自学编程【不走弯路】

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
/**
 * 启动类
 *
 * @author <a href="https://github.com/jarvischan666">JarvisChan</a>
 * @from <a href="https://jvc.icu">编程导航知识星球</a>
 */
@SpringBootApplication
@MapperScan("com.jvc.matematch.mapper")
@EnableScheduling
@EnableRedisHttpSession // 添加这行注解，开启redis作为spring session
public class MateMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MateMatchApplication.class, args);
    }

}

// https://github.com/jarvischan666