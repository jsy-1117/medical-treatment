package com.medical.smart_medical_server.BackendTest.ai;

import com.medical.smart_medical_server.service.impl.GeminiServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GeminiServiceImpl 动态上下文截断测试。
 * 与 GeminiChatStreamTest 分离以避免 @MockBean GeminiService 冲突。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("AI上下文截断测试")
class GeminiContextTruncationTest {

    @Autowired
    private GeminiServiceImpl geminiService;

    @Test
    @DisplayName("动态上下文超过 2000 字符时自动截断")
    void shouldTruncateContextWhenOverLimit() {
        String context = geminiService.getDynamicContext();
        // 即使数据库没有数据，上下文也不会为 null
        assertThat(context).isNotNull();
        // MAX_CONTEXT_CHARS = 2000，截断后应 ≤ 2000 + 截断标记长度
        assertThat(context.length()).isLessThanOrEqualTo(2015);
    }
}
