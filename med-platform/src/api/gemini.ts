import request from '@/utils/request';
import type { ChatMessage, ChatRequest, ChatResponse } from '@/types/chat';

/**
 * 流式回调接口
 */
export interface StreamCallbacks {
    onDelta: (content: string) => void;
    onDone: (metadata: Partial<ChatResponse>) => void;
    onError: (error: Error) => void;
    /** 用户主动取消流时调用（如发新消息、组件卸载） */
    onCancel?: () => void;
}

/**
 * Gemini AI API
 */
export const geminiApi = {
    /**
     * 发送聊天消息（同步）
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
    },

    /**
     * 流式聊天 — 使用原生 fetch 解析 SSE
     * 30s 超时 + 用户可取消（返回 AbortController）
     * @returns AbortController 用于取消请求
     */
    chatStream(data: ChatRequest, callbacks: StreamCallbacks): AbortController {
        const controller = new AbortController();
        let isTimeout = false;

        // 90s 超时：DeepSeek 响应较慢，等待首字返回后即流畅
        const timeoutId = setTimeout(() => {
            isTimeout = true;
            controller.abort();
        }, 90000);

        const baseUrl = import.meta.env.VITE_API_BASE_URL || '';
        const url = `${baseUrl}/api/ai/chat/stream`;

        fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
            signal: controller.signal,
        })
            .then(async (response) => {
                if (!response.ok) {
                    clearTimeout(timeoutId);
                    throw new Error(`HTTP ${response.status}`);
                }

                const reader = response.body?.getReader();
                if (!reader) throw new Error('Response body is not readable');

                const decoder = new TextDecoder();
                let buffer = '';

                while (true) {
                    const { done, value } = await reader.read();
                    if (done) break;

                    buffer += decoder.decode(value, { stream: true });

                    // 按 \n\n 分割完整 event
                    const events = buffer.split('\n\n');
                    buffer = events.pop() || '';

                    for (const eventBlock of events) {
                        processSseEvent(eventBlock, callbacks);
                    }
                }

                clearTimeout(timeoutId);

                // 处理剩余数据
                if (buffer.trim()) {
                    processSseEvent(buffer, callbacks);
                }
            })
            .catch((error: Error) => {
                clearTimeout(timeoutId);
                if (error.name === 'AbortError') {
                    if (isTimeout) {
                        callbacks.onError(new Error('连接超时，请重试'));
                    } else {
                        callbacks.onCancel?.();
                    }
                    return;
                }
                callbacks.onError(error);
            });

        return controller;
    },
};

/**
 * 解析单个 SSE event 块并调用对应回调
 */
function processSseEvent(eventBlock: string, callbacks: StreamCallbacks): void {
    const lines = eventBlock.split('\n');
    let eventType = '';
    let data = '';

    for (const line of lines) {
        if (line.startsWith('event: ')) {
            eventType = line.slice(7).trim();
        } else if (line.startsWith('data: ')) {
            // SSE 规范允许多行 data:，需拼接而非覆盖
            if (data) {
                data += '\n';
            }
            data += line.slice(6);
        }
    }

    if (!data) return;

    switch (eventType) {
        case 'delta':
            callbacks.onDelta(data);
            break;
        case 'done':
            try {
                const metadata = JSON.parse(data);
                callbacks.onDone(metadata);
            } catch {
                console.warn('Failed to parse done metadata:', data);
            }
            break;
    }
}
