package com.medical.smart_medical_server.BackendTest;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 后端集成测试的公共 Mock 配置。
 * 使用 @Profile("test") 确保仅在测试环境下加载，
 * 与 @ActiveProfiles("test") 配合使用。
 * 使用 @Configuration 而非 @TestConfiguration 以支持自动扫描。
 */
@Configuration
@Profile("test")
public class BackendTestMockConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }
}
