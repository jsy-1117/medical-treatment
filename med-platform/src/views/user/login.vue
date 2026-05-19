<template>
    <div class="auth-wrapper">
        <div class="auth-container">
            <div class="auth-header">
                <div class="brand">
                    <div class="brand-mark">+</div>
                    <span class="brand-name">智慧医疗平台</span>
                </div>
                <p class="brand-slogan">连接优质医疗资源，守护您的健康生活</p>
            </div>

            <div class="auth-card">
                <div class="auth-card-title">
                    <h3>欢迎回来</h3>
                    <p>请登录您的个人账号</p>
                </div>

                <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" class="auth-form">
                    <el-form-item prop="username">
                        <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" clearable />
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock"
                            show-password clearable @keyup.enter="handleLogin" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" :loading="loading" class="submit-btn" round @click="handleLogin">
                            立即登录
                        </el-button>
                    </el-form-item>

                    <div class="auth-footer">
                        <span class="text-gray">还没有账号？</span>
                        <el-button type="primary" link @click="$router.push('/user/register')">
                            免费注册
                        </el-button>
                        <el-divider direction="vertical" />
                        <el-button link type="info" @click="$router.push('/')">
                            返回首页
                        </el-button>
                    </div>
                </el-form>
            </div>

            <div class="auth-bottom">
                <p>&copy; 2025 Smart Medical Service. All rights reserved.</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { usePatientStore } from '@/stores/patient';
import type { FormInstance, FormRules } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';

const patientStore = usePatientStore();

const loginFormRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive({
    username: '',
    password: ''
});

const loginRules: FormRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ]
};

const handleLogin = async () => {
    if (!loginFormRef.value) return;

    await loginFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true;
            try {
                await patientStore.login(loginForm);
            } catch (error) {
                console.error('登录失败', error);
            } finally {
                loading.value = false;
            }
        }
    });
};
</script>

<style scoped lang="scss">
.auth-wrapper {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
    background: #f7f8f5;
    background-image:
        linear-gradient(rgba(26,138,122,0.03) 1px, transparent 1px),
        linear-gradient(90deg, rgba(26,138,122,0.03) 1px, transparent 1px);
    background-size: 40px 40px;
    font-family: 'DM Sans', 'Noto Sans SC', -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
    -webkit-font-smoothing: antialiased;
    position: relative;
    overflow: hidden;
}

.auth-container {
    width: 100%;
    max-width: 400px;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.auth-header {
    text-align: center;
    margin-bottom: 28px;

    .brand {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        margin-bottom: 10px;

        .brand-mark {
            width: 36px;
            height: 36px;
            border-radius: 8px;
            background: linear-gradient(135deg, #1a8a7a, #2db8a0);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 16px;
            font-weight: 700;
            box-shadow: 0 4px 8px rgba(26, 138, 122, 0.25);
        }

        .brand-name {
            font-size: 22px;
            font-weight: 700;
            color: #1a2a2a;
            letter-spacing: -0.5px;
        }
    }

    .brand-slogan {
        font-size: 14px;
        color: #5a6a6a;
        margin: 0;
    }
}

.auth-card {
    width: 100%;
    background: white;
    border-radius: 16px;
    padding: 36px 32px 32px;
    box-shadow: 0 8px 28px rgba(26, 138, 122, 0.08);
}

.auth-card-title {
    margin-bottom: 24px;

    h3 {
        margin: 0 0 6px;
        font-size: 22px;
        font-weight: 700;
        color: #1a2a2a;
    }

    p {
        margin: 0;
        font-size: 14px;
        color: #5a6a6a;
    }
}

.submit-btn {
    width: 100%;
    padding: 22px 0;
    font-size: 16px;
    font-weight: 600;
    margin-top: 10px;
    background: #1a8a7a;
    border-color: #1a8a7a;
    transition: all 0.3s;

    &:hover {
        background: #147a6b;
        border-color: #147a6b;
        transform: translateY(-1px);
        box-shadow: 0 6px 12px rgba(26, 138, 122, 0.25);
    }
}

.auth-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 20px;
    font-size: 14px;

    .text-gray {
        color: #9aabab;
    }
}

.auth-bottom {
    margin-top: 32px;
    text-align: center;
    color: #9aabab;
    font-size: 12px;
}
</style>
