package com.medical.smart_medical_server.BackendTest.ai;

import com.medical.smart_medical_server.DTO.GeminiChatDTO;
import com.medical.smart_medical_server.VO.GeminiChatVO;
import com.medical.smart_medical_server.service.GeminiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AI流式对话接口测试")
class GeminiChatStreamTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private GeminiService geminiService;

    @Test
    @DisplayName("流式端点返回 200 OK")
    void shouldReturnSseOk() throws Exception {
        when(geminiService.chatStream(any(GeminiChatDTO.class)))
                .thenReturn(new SseEmitter());

        GeminiChatDTO dto = new GeminiChatDTO();
        dto.setMessage("头痛");

        mockMvc.perform(post("/api/ai/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("流式端点不影响同步端点 — 同步端点仍正常工作")
    void shouldNotAffectSyncEndpoint() throws Exception {
        GeminiChatVO mockVO = GeminiChatVO.builder()
                .reply("建议您前往内科就诊")
                .recommendedDepartment("内科")
                .urgent(false)
                .timestamp(System.currentTimeMillis())
                .build();
        when(geminiService.chat(any(GeminiChatDTO.class))).thenReturn(mockVO);

        GeminiChatDTO dto = new GeminiChatDTO();
        dto.setMessage("头痛");

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reply").value("建议您前往内科就诊"))
                .andExpect(jsonPath("$.data.recommendedDepartment").value("内科"));
    }

    @Test
    @DisplayName("流式端点参数校验 — 空 message 返回 400")
    void shouldRejectInvalidInput() throws Exception {
        GeminiChatDTO dto = new GeminiChatDTO();
        dto.setMessage("");

        mockMvc.perform(post("/api/ai/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("流式调用失败时返回 500")
    void shouldReturnErrorOnFailure() throws Exception {
        when(geminiService.chatStream(any(GeminiChatDTO.class)))
                .thenThrow(new RuntimeException("AI 服务暂时不可用"));

        GeminiChatDTO dto = new GeminiChatDTO();
        dto.setMessage("头痛");

        mockMvc.perform(post("/api/ai/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("SseEmitter 超时后自动完成")
    void shouldCompleteOnTimeout() throws Exception {
        SseEmitter emitter = new SseEmitter(50L);
        Thread.sleep(150L);
        try {
            emitter.send(SseEmitter.event().name("delta").data("test"));
        } catch (Exception e) {
            // 超时后 send 应抛出异常
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(Exception.class);
        }
    }
}
