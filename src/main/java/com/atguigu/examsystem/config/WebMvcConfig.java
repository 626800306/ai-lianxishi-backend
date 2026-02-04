package com.atguigu.examsystem.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Component
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径跨域访问
                .allowedOrigins("*") // 允许所有域名访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许GET、POST、PUT、DELETE、OPTIONS请求方法访问
                .allowedHeaders("*") // 运行所有请求头访问
                .allowCredentials(false) // 是否运行返送Cookie
                .maxAge(3600); // 最大请求时间 单位为妙
    }
}
