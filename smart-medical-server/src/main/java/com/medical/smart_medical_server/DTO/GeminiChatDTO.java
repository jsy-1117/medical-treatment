package com.medical.smart_medical_server.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * Gemini 聊天请求 DTO
 */
@Data
public class GeminiChatDTO {

    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 会话历史（可选）
     * 用于保持上下文连贯性
     */
    private List<ChatMessage> history;

    /**
     * 聊天消息内部类
     */
    @Data
    public static class ChatMessage {
        /**
         * 角色：user 或 assistant
         */
        private String role;

        /**
         * 消息内容
         */
        private String content;
    }
}