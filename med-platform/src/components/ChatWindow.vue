<template>
    <div class="chat-window-container">
        <div v-if="!isOpen" class="chat-trigger-wrapper" @click="toggleChat">
            <el-button class="chat-trigger-btn" type="primary" circle>
                <el-icon :size="28">
                    <ChatDotRound />
                </el-icon>
            </el-button>
            <span class="trigger-label">智能导诊</span>
        </div>

        <transition name="el-zoom-in-bottom">
            <div v-if="isOpen" class="chat-window">
                <div class="chat-header">
                    <div class="header-left">
                        <div class="avatar-box">
                            <el-icon>
                                <Service />
                            </el-icon>
                        </div>
                        <div class="header-info">
                            <h3>智能导诊助手</h3>
                            <div class="status-row">
                                <span class="status-dot"></span>
                                <span class="status-text">AI 在线为您服务</span>
                            </div>
                        </div>
                    </div>
                    <div class="header-actions">
                        <el-button link class="close-btn" @click="toggleChat">
                            <el-icon :size="20">
                                <Close />
                            </el-icon>
                        </el-button>
                    </div>
                </div>

                <div ref="messageListRef" class="message-list">
                    <div v-if="messages.length === 0" class="welcome-card">
                        <div class="welcome-icon">
                            <el-icon>
                                <FirstAidKit />
                            </el-icon>
                        </div>
                        <h4>您好！我是您的医疗助手</h4>
                        <p>请描述您的症状（如：头痛、发烧），我将为您推荐合适的科室。</p>
                    </div>

                    <div v-for="(msg, index) in messages" :key="index" :class="['message-row', msg.role]">
                        <div class="message-avatar">
                            <el-avatar :size="36" :icon="msg.role === 'assistant' ? 'Cpu' : 'UserFilled'"
                                :class="msg.role === 'assistant' ? 'ai-avatar' : 'user-avatar'" />
                        </div>

                        <div class="message-content">
                            <div class="message-bubble">
                                {{ msg.content }}
                            </div>
                            <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
                        </div>
                    </div>

                    <div v-if="loading" class="message-row assistant">
                        <div class="message-avatar">
                            <el-avatar :size="36" icon="Cpu" class="ai-avatar" />
                        </div>
                        <div class="message-content">
                            <div class="message-bubble typing-bubble">
                                <span class="dot"></span>
                                <span class="dot"></span>
                                <span class="dot"></span>
                            </div>
                        </div>
                    </div>

                    <!-- 推荐科室导航 -->
                    <div v-if="recommendedDept && recommendedDept.id" class="recommend-card">
                        <div class="recommend-icon">
                            <el-icon><FirstAidKit /></el-icon>
                        </div>
                        <div class="recommend-info">
                            <div class="recommend-label">推荐您前往</div>
                            <div class="recommend-dept">{{ recommendedDept.name }}</div>
                        </div>
                        <el-button type="primary" class="recommend-btn" @click="goToBook">
                            去挂号
                            <el-icon><ArrowRight /></el-icon>
                        </el-button>
                    </div>
                </div>

                <div class="chat-footer">
                    <div v-if="messages.length === 0" class="quick-questions">
                        <div class="quick-title">猜您想问：</div>
                        <div class="tags-wrapper">
                            <el-tag v-for="(question, index) in quickQuestions" :key="index" class="quick-tag"
                                effect="plain" round @click="askQuestion(question)">
                                {{ question }}
                            </el-tag>
                        </div>
                    </div>

                    <div class="input-area">
                        <el-input v-model="inputMessage" placeholder="请描述您的症状..." :disabled="loading"
                            @keyup.enter="sendMessage" class="custom-input">
                            <template #suffix>
                                <el-button type="primary" circle class="send-btn" :loading="loading"
                                    :disabled="!inputMessage.trim()" @click="sendMessage">
                                    <el-icon v-if="!loading">
                                        <Position />
                                    </el-icon>
                                </el-button>
                            </template>
                        </el-input>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
// 引入图标：增加 Service, FirstAidKit, Position 等
import {
    ChatDotRound, Close, Cpu, UserFilled, Position,
    Service, FirstAidKit, ArrowRight
} from '@element-plus/icons-vue';
import { geminiApi } from '@/api/gemini';
import type { ChatMessage } from '@/types/chat';

// 状态
const isOpen = ref(false);
const messages = ref<ChatMessage[]>([]);
const inputMessage = ref('');
const loading = ref(false);
const messageListRef = ref<HTMLElement>();
const router = useRouter();

// 最新推荐科室信息
const recommendedDept = ref<{ name: string; id: number } | null>(null);

// 快捷问题
const quickQuestions = ref([
    '头痛发热应该挂什么科？',
    '胃痛应该看哪个科室？',
    '如何预约专家门诊？',
    '挂号费是多少？'
]);

// 切换聊天窗口
const toggleChat = () => {
    isOpen.value = !isOpen.value;
    if (isOpen.value) {
        nextTick(() => {
            scrollToBottom();
        });
    }
};

// 发送消息
const sendMessage = async () => {
    const text = inputMessage.value.trim();
    if (!text || loading.value) return;

    const userMessage: ChatMessage = {
        role: 'user',
        content: text,
        timestamp: Date.now()
    };

    messages.value.push(userMessage);
    inputMessage.value = '';
    recommendedDept.value = null; // 清空上一条推荐

    loading.value = true;
    scrollToBottom();

    try {
        const response = await geminiApi.chat({
            message: text,
            history: messages.value.slice(0, -1) // 排除刚发的消息
        });

        const assistantMessage: ChatMessage = {
            role: 'assistant',
            content: response.data.reply,
            timestamp: response.data.timestamp
        };

        messages.value.push(assistantMessage);

        // 存储推荐科室信息
        if (response.data.recommendedDepartment && response.data.recommendedDeptId) {
            recommendedDept.value = {
                name: response.data.recommendedDepartment,
                id: response.data.recommendedDeptId
            };
        }

        if (response.data.urgent) {
            ElMessage.warning('检测到紧急症状，建议尽快就医！');
        }
    } catch (error) {
        console.error('发送消息失败:', error);
        ElMessage.error('服务繁忙，请稍后再试');
    } finally {
        loading.value = false;
        scrollToBottom();
    }
};

const askQuestion = (question: string) => {
    inputMessage.value = question;
    sendMessage();
};

const goToBook = () => {
    if (recommendedDept.value?.id) {
        router.push(`/book?deptId=${recommendedDept.value.id}`);
    }
};

const scrollToBottom = () => {
    nextTick(() => {
        if (messageListRef.value) {
            messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
        }
    });
};

const formatTime = (timestamp?: number) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};
</script>

<style scoped lang="scss">
/* 颜色变量 - 医疗蓝主题 */
$primary-color: #3b82f6;
$primary-gradient: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
$bg-light: #f8fafc;
$text-main: #1e293b;
$text-secondary: #64748b;

.chat-window-container {
    position: fixed;
    bottom: 40px;
    right: 40px;
    z-index: 2000;
}

/* 1. 浮动触发按钮 */
.chat-trigger-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
    animation: fadeIn 0.5s ease;

    .chat-trigger-btn {
        width: 64px;
        height: 64px;
        font-size: 28px;
        background: $primary-gradient;
        border: none;
        box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
        transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);

        &:hover {
            transform: scale(1.1) translateY(-2px);
            box-shadow: 0 12px 24px rgba(59, 130, 246, 0.5);
        }
    }

    .trigger-label {
        margin-top: 8px;
        font-size: 12px;
        font-weight: 600;
        color: $primary-color;
        background: white;
        padding: 4px 10px;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        opacity: 0;
        transform: translateY(5px);
        transition: all 0.3s;
    }

    &:hover .trigger-label {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 2. 聊天主窗口 */
.chat-window {
    width: 380px;
    height: 600px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    border: 1px solid rgba(0, 0, 0, 0.05);

    /* 头部 */
    .chat-header {
        padding: 16px 20px;
        background: $primary-gradient;
        color: #fff;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .header-left {
            display: flex;
            align-items: center;
            gap: 12px;

            .avatar-box {
                width: 40px;
                height: 40px;
                background: rgba(255, 255, 255, 0.2);
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 24px;
            }

            .header-info {
                h3 {
                    margin: 0;
                    font-size: 16px;
                    font-weight: 600;
                    letter-spacing: 0.5px;
                }

                .status-row {
                    display: flex;
                    align-items: center;
                    gap: 6px;
                    margin-top: 2px;

                    .status-dot {
                        width: 6px;
                        height: 6px;
                        background: #10b981;
                        /* 在线绿 */
                        border-radius: 50%;
                        box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.3);
                    }

                    .status-text {
                        font-size: 12px;
                        opacity: 0.9;
                    }
                }
            }
        }

        .close-btn {
            color: rgba(255, 255, 255, 0.8);

            &:hover {
                color: white;
                transform: rotate(90deg);
                transition: transform 0.3s;
            }
        }
    }

    /* 消息列表 */
    .message-list {
        flex: 1;
        background: $bg-light;
        padding: 20px;
        overflow-y: auto;

        /* 欢迎卡片 */
        .welcome-card {
            background: white;
            padding: 30px 20px;
            border-radius: 12px;
            text-align: center;
            border: 1px solid #e2e8f0;
            margin-top: 20px;

            .welcome-icon {
                color: $primary-color;
                font-size: 48px;
                margin-bottom: 16px;
                background: #eff6ff;
                width: 80px;
                height: 80px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto 16px;
            }

            h4 {
                color: $text-main;
                margin: 0 0 8px;
                font-size: 16px;
            }

            p {
                color: $text-secondary;
                font-size: 13px;
                line-height: 1.5;
                margin: 0;
            }
        }

        /* 消息行 */
        .message-row {
            display: flex;
            gap: 12px;
            margin-bottom: 20px;
            animation: slideUp 0.3s ease;

            /* 用户消息 */
            &.user {
                flex-direction: row-reverse;

                .message-bubble {
                    background: $primary-color;
                    color: white;
                    border-radius: 12px 2px 12px 12px;
                    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
                }

                .message-time {
                    text-align: right;
                }
            }

            /* AI 消息 */
            &.assistant {
                .message-bubble {
                    background: white;
                    color: $text-main;
                    border: 1px solid #e2e8f0;
                    border-radius: 2px 12px 12px 12px;
                }

                .ai-avatar {
                    background: #eff6ff;
                    color: $primary-color;
                }
            }

            .message-avatar {
                flex-shrink: 0;
                margin-top: 2px;
            }

            .message-content {
                max-width: 75%;

                .message-bubble {
                    padding: 10px 14px;
                    font-size: 14px;
                    line-height: 1.6;
                    word-wrap: break-word;
                }

                .message-time {
                    display: block;
                    font-size: 11px;
                    color: #94a3b8;
                    margin-top: 4px;
                }
            }
        }

        /* 正在输入动画 */
        .typing-bubble {
            display: flex;
            gap: 4px;
            padding: 14px 18px !important;

            .dot {
                width: 6px;
                height: 6px;
                background: #cbd5e1;
                border-radius: 50%;
                animation: typing 1.4s infinite ease-in-out;

                &:nth-child(1) {
                    animation-delay: 0s;
                }

                &:nth-child(2) {
                    animation-delay: 0.2s;
                }

                &:nth-child(3) {
                    animation-delay: 0.4s;
                }
            }
        }

        /* 推荐科室卡片 */
        .recommend-card {
            display: flex;
            align-items: center;
            gap: 12px;
            background: linear-gradient(135deg, #eff6ff 0%, #f0fdf4 100%);
            border: 1px solid #bfdbfe;
            border-radius: 12px;
            padding: 12px 16px;
            margin: 0 0 16px;
            animation: slideUp 0.3s ease;

            .recommend-icon {
                width: 40px;
                height: 40px;
                background: #3b82f6;
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 20px;
                flex-shrink: 0;
            }

            .recommend-info {
                flex: 1;
                min-width: 0;

                .recommend-label {
                    font-size: 12px;
                    color: #64748b;
                    margin-bottom: 2px;
                }

                .recommend-dept {
                    font-size: 15px;
                    font-weight: 600;
                    color: #1e293b;
                }
            }

            .recommend-btn {
                flex-shrink: 0;
                border-radius: 8px;
                font-weight: 500;
            }
        }
    }

    /* 底部输入区 */
    .chat-footer {
        background: white;
        border-top: 1px solid #e2e8f0;

        .quick-questions {
            padding: 12px 16px 0;

            .quick-title {
                font-size: 12px;
                color: #94a3b8;
                margin-bottom: 8px;
            }

            .tags-wrapper {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;

                .quick-tag {
                    cursor: pointer;
                    border-color: #e2e8f0;
                    color: $text-secondary;
                    transition: all 0.2s;

                    &:hover {
                        color: $primary-color;
                        border-color: $primary-color;
                        background: #eff6ff;
                    }
                }
            }
        }

        .input-area {
            padding: 12px 16px 16px;

            .custom-input {
                :deep(.el-input__wrapper) {
                    border-radius: 24px;
                    padding-right: 4px;
                    /* 为按钮留空间 */
                    background-color: #f8fafc;
                    box-shadow: none;
                    border: 1px solid #e2e8f0;

                    &.is-focus {
                        background-color: white;
                        border-color: $primary-color;
                        box-shadow: 0 0 0 1px $primary-color;
                    }
                }

                :deep(.el-input__inner) {
                    height: 44px;
                }
            }

            .send-btn {
                width: 36px;
                height: 36px;
                margin-right: -6px;

                &:disabled {
                    background-color: #e2e8f0;
                    border-color: #e2e8f0;
                }
            }
        }
    }
}

@keyframes typing {

    0%,
    100% {
        transform: translateY(0);
    }

    50% {
        transform: translateY(-4px);
    }
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}
</style>