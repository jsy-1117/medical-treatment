package com.medical.smart_medical_server.service;

import com.medical.smart_medical_server.DTO.GeminiChatDTO;
import com.medical.smart_medical_server.VO.GeminiChatVO;

/**
 * AI 导诊服务接口
 */
public interface GeminiService {

    /**
     * 智能对话
     *
     * @param chatDTO 聊天请求
     * @return 聊天响应
     */
    GeminiChatVO chat(GeminiChatDTO chatDTO);

    /**
     * 症状导诊
     *
     * @param symptoms 症状描述
     * @return 推荐科室和建议
     */
    GeminiChatVO recommendDepartment(String symptoms);

    /**
     * 获取动态上下文（科室和医生的公开信息）
     * 从 Redis 缓存读取，用于增强 LLM 提示词
     *
     * @return 格式化的上下文字符串
     */
    String getDynamicContext();
}