package com.medical.smart_medical_server.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gemini 聊天响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiChatVO {

    /**
     * AI 回复内容
     */
    private String reply;

    /**
     * 推荐的科室（如果有）
     */
    private String recommendedDepartment;

    /**
     * 是否需要紧急就医
     */
    private Boolean urgent;

    /**
     * 响应时间戳
     */
    private Long timestamp;
}