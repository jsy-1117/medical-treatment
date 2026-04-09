import request from '@/utils/request';
import type { ChatMessage, ChatRequest, ChatResponse } from '@/types/chat';

/**
 * Gemini AI API
 */
export const geminiApi = {
    /**
     * 发送聊天消息
     */
    chat(data: ChatRequest) {
        return request<ChatResponse>({
            url: '/api/ai/chat',
            method: 'POST',
            data
        });
    },

    /**
     * 获取科室推荐
     */
    recommendDepartment(symptoms: string) {
        return request<ChatResponse>({
            url: '/api/ai/recommend',
            method: 'POST',
            params: { symptoms }
        });
    },

    /**
     * 健康检查
     */
    health() {
        return request<string>({
            url: '/api/ai/health',
            method: 'GET'
        });
    }
};