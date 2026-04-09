<template>
    <div class="login-wrapper">
        <div class="login-container">
            <div class="login-header">
                <div class="brand">
                    <div class="logo-box">
                        <el-icon :size="28">
                            <FirstAidKit />
                        </el-icon>
                    </div>
                    <span class="brand-name">智慧医疗平台</span>
                </div>
                <p class="brand-slogan">连接优质医疗资源，守护您的健康生活</p>
            </div>

            <el-card class="login-card" shadow="hover">
                <div class="card-title">
                    <h3>欢迎回来</h3>
                    <p>请登录您的个人账号</p>
                </div>

                <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" class="login-form">
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

                    <div class="form-footer">
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
            </el-card>

            <div class="login-footer">
                <p>&copy; 2025 Smart Medical Service. All rights reserved.</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { usePatientStore } from '@/stores/patient';
import type { FormInstance, FormRules } from 'element-plus';
// 引入图标
import { User, Lock, FirstAidKit } from '@element-plus/icons-vue';

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
.login-wrapper {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    /* 背景：柔和的蓝灰渐变，比原来的高饱和度更耐看 */
    background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
    position: relative;
    overflow: hidden;

    /* 增加背景装饰圆，增加层次感 */
    &::before {
        content: '';
        position: absolute;
        top: -100px;
        right: -100px;
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, rgba(59, 130, 246, 0.1) 0%, rgba(255, 255, 255, 0) 70%);
        border-radius: 50%;
    }
}

.login-container {
    width: 100%;
    max-width: 420px;
    padding: 20px;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.login-header {
    text-align: center;
    margin-bottom: 30px;

    .brand {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        margin-bottom: 10px;

        .logo-box {
            width: 40px;
            height: 40px;
            background: #3b82f6;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            box-shadow: 0 4px 6px rgba(59, 130, 246, 0.3);
        }

        .brand-name {
            font-size: 24px;
            font-weight: 700;
            color: #1e293b;
            letter-spacing: -0.5px;
        }
    }

    .brand-slogan {
        font-size: 14px;
        color: #64748b;
        margin: 0;
    }
}

.login-card {
    width: 100%;
    border-radius: 16px;
    border: none;
    /* 柔和的阴影，制造悬浮感 */
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.01);

    :deep(.el-card__body) {
        padding: 40px 32px;
    }

    .card-title {
        margin-bottom: 24px;

        h3 {
            margin: 0 0 8px;
            font-size: 22px;
            color: #1e293b;
            font-weight: 600;
        }

        p {
            margin: 0;
            font-size: 14px;
            color: #94a3b8;
        }
    }
}

.login-form {
    .submit-btn {
        width: 100%;
        padding: 22px 0;
        font-size: 16px;
        font-weight: 600;
        margin-top: 10px;
        box-shadow: 0 4px 6px rgba(59, 130, 246, 0.2);
        transition: all 0.3s;

        &:hover {
            transform: translateY(-1px);
            box-shadow: 0 6px 10px rgba(59, 130, 246, 0.3);
        }
    }

    .form-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: 20px;
        font-size: 14px;

        .text-gray {
            color: #94a3b8;
        }
    }
}

.login-footer {
    margin-top: 40px;
    text-align: center;
    color: #94a3b8;
    font-size: 12px;
}
</style>