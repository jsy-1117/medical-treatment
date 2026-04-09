/**
 * 聊天消息类型
 */
export interface ChatMessage {
    role: 'user' | 'assistant';
    content: string;
    timestamp?: number;
}

/**
 * 聊天请求类型
 */
export interface ChatRequest {
    message: string;
    history?: ChatMessage[];
}

/**
 * 聊天响应类型
 */
export interface ChatResponse {
    reply: string;
    recommendedDepartment?: string;
    urgent?: boolean;
    timestamp: number;
}