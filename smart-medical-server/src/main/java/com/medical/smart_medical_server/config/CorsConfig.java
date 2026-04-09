package com.medical.smart_medical_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 * 解决前后端分离项目的跨域问题
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有源（开发环境）
        // 生产环境建议改为具体的前端域名
        config.addAllowedOriginPattern("*");

        // 或者只允许特定源（更安全）
        // config.addAllowedOrigin("http://localhost:5173");
        // config.addAllowedOrigin("http://127.0.0.1:5173");
        // config.addAllowedOrigin("https://your-production-domain.com");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有请求方法（GET, POST, PUT, DELETE 等）
        config.addAllowedMethod("*");

        // 允许携带凭证（如 Cookie、Authorization 等）
        config.setAllowCredentials(true);

        // 预检请求的有效期（秒）
        // 设置为 1 小时，减少预检请求次数
        config.setMaxAge(3600L);

        // 注册 CORS 配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}