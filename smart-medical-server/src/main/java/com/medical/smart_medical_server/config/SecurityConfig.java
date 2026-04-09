package com.medical.smart_medical_server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                // 禁用 CSRF（前后端分离项目）
                .csrf(csrf -> csrf.disable())

                // 配置 Session 管理（JWT 无状态）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // ==========================================
                        // 公开接口（无需认证）
                        // ==========================================

                        // 管理员登录、注册
                        .requestMatchers(
                                "/api/admin/login",
                                "/api/admin/register",
                                "/api/patient/login",      // 添加
                                "/api/patient/register"
                        ).permitAll()

                        // 用户端公开接口（查看科室、医生、排班信息）
                        .requestMatchers("/api/public/**").permitAll()

                        // 患者端接口（患者挂号相关）
                        .requestMatchers("/api/patient/**").permitAll()

                        // 错误页面
                        .requestMatchers("/error").permitAll()

                        // ==========================================
                        // 需要认证的接口
                        // ==========================================

                        // 管理员接口（需要管理员认证）
                        .requestMatchers("/api/admin/**").authenticated()
                        .requestMatchers("/api/doctor/**").authenticated()
                        .requestMatchers("/api/department/**").authenticated()
                        .requestMatchers("/api/schedule/**").authenticated()
                        .requestMatchers("/api/appointment/**").authenticated()

                         // 医生端登录
                        .requestMatchers("/api/doctor-auth/login").permitAll()

                        // 医生端接口（需要医生认证）
                        .requestMatchers("/api/doctor-auth/**").authenticated()
                        .requestMatchers("/api/doctor-portal/**").authenticated()

                        .requestMatchers("/api/ai/**").permitAll()  // 允许匿名访问 AI 接口

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}