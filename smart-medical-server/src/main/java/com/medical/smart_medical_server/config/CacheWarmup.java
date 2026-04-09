package com.medical.smart_medical_server.config;

import com.medical.smart_medical_server.service.GeminiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 缓存预热组件
 * 应用启动后自动预热 LLM 上下文缓存，避免首次请求延迟
 */
@Slf4j
@Component
public class CacheWarmup implements ApplicationRunner {

    @Autowired
    private GeminiService geminiService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("开始预热 LLM 上下文缓存...");
            long start = System.currentTimeMillis();

            // 预热缓存
            String context = geminiService.getDynamicContext();

            long cost = System.currentTimeMillis() - start;
            log.info("LLM 上下文缓存预热完成，耗时 {}ms，上下文长度 {} 字符", cost, context.length());
        } catch (Exception e) {
            log.warn("LLM 上下文缓存预热失败，不影响正常使用: {}", e.getMessage());
        }
    }
}