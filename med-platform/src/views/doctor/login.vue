<template>
    <div class="doctor-login-wrapper">
        <div class="login-container">
            <div class="login-header">
                <div class="logo-box">
                    <el-icon :size="32">
                        <Stethoscope />
                    </el-icon>
                </div>
                <div class="header-text">
                    <h1 class="system-name">医生工作站</h1>
                    <p class="system-sub">Doctor Workstation</p>
                </div>
            </div>

            <el-card class="login-card" shadow="hover">
                <div class="card-intro">
                    <h3>欢迎登录</h3>
                    <p>请输入您的医师账号进行操作</p>
                </div>

                <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" class="login-form"
                    @submit.prevent>
                    <el-form-item prop="username">
                        <el-input v-model="loginForm.username" placeholder="请输入医师账号" :prefix-icon="User" clearable
                            tabindex="1" />
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input v-model="loginForm.password" type="password" placeholder="请输入登录密码" :prefix-icon="Lock"
                            show-password clearable tabindex="2" @keyup.enter="handleLogin" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" :loading="loading" class="submit-btn" round @click="handleLogin">
                            安全登录
                        </el-button>
                    </el-form-item>

                    <div class="form-footer">
                        <el-button link class="footer-link" @click="$router.push('/')">
                            <el-icon class="mr-1">
                                <HomeFilled />
                            </el-icon> 返回门户首页
                        </el-button>
                    </div>
                </el-form>
            </el-card>

            <div class="copyright">
                &copy; 2024 Smart Medical System · For Medical Professionals
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useDoctorStore } from '@/stores/doctor';
import type { FormInstance, FormRules } from 'element-plus';
// 引入图标 (如果没有安装 Stethoscope，可用 FirstAidKit 代替)
import { User, Lock, HomeFilled } from '@element-plus/icons-vue';
// 这里假设你有 Stethoscope 图标，如果没有，请使用 FirstAidKit
// import { FirstAidKit as Stethoscope } from '@element-plus/icons-vue'; 
// 为了演示，我们先用一个通用图标做别名，或者你直接用 FirstAidKit
import { FirstAidKit as Stethoscope } from '@element-plus/icons-vue';

const doctorStore = useDoctorStore();

const loginFormRef = ref<FormInstance>();
const loading = ref(false);

const loginForm = reactive({
    username: '',
    password: ''
});

const loginRules: FormRules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const handleLogin = async () => {
    if (!loginFormRef.value) return;

    await loginFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true;
            try {
                await doctorStore.login(loginForm);
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
/* 定义紫色系变量 */
$primary-purple: #7c3aed;
$light-purple-bg: #f3e8ff;
$dark-text: #1f2937;
$light-text: #6b7280;

.doctor-login-wrapper {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    /* 背景：柔和的淡紫色渐变 */
    background: linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%);
    position: relative;
    overflow: hidden;
    font-family: 'Inter', sans-serif;

    /* 背景装饰圆 */
    &::before {
        content: '';
        position: absolute;
        top: -100px;
        right: -100px;
        width: 400px;
        height: 400px;
        background: radial-gradient(circle, rgba(139, 92, 246, 0.1) 0%, rgba(255, 255, 255, 0) 70%);
        border-radius: 50%;
    }
}

.login-container {
    width: 100%;
    max-width: 400px;
    padding: 20px;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
}

/* 头部品牌 */
.login-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 30px;
    text-align: center;

    .logo-box {
        width: 56px;
        height: 56px;
        background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        box-shadow: 0 8px 20px rgba(124, 58, 237, 0.3);
        margin-bottom: 16px;
    }

    .header-text {
        .system-name {
            font-size: 24px;
            font-weight: 700;
            color: $dark-text;
            margin: 0 0 4px;
            letter-spacing: -0.5px;
        }

        .system-sub {
            color: $light-text;
            font-size: 13px;
            margin: 0;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 500;
        }
    }
}

/* 卡片样式 */
.login-card {
    width: 100%;
    border-radius: 16px;
    border: none;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05), 0 10px 10px -5px rgba(0, 0, 0, 0.01);
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);

    :deep(.el-card__body) {
        padding: 40px 32px;
    }

    .card-intro {
        text-align: center;
        margin-bottom: 24px;

        h3 {
            margin: 0 0 8px;
            font-size: 20px;
            color: $dark-text;
            font-weight: 600;
        }

        p {
            margin: 0;
            font-size: 14px;
            color: $light-text;
        }
    }
}

/* 表单样式 */
.login-form {
    .submit-btn {
        width: 100%;
        padding: 22px 0;
        font-size: 16px;
        font-weight: 600;
        background-color: $primary-purple;
        border-color: $primary-purple;
        margin-top: 10px;
        box-shadow: 0 4px 12px rgba(124, 58, 237, 0.25);
        transition: all 0.3s;

        &:hover {
            background-color: #6d28d9;
            border-color: #6d28d9;
            transform: translateY(-1px);
            box-shadow: 0 6px 16px rgba(124, 58, 237, 0.35);
        }
    }

    .form-footer {
        display: flex;
        justify-content: center;
        margin-top: 24px;

        .footer-link {
            color: $light-text;
            font-size: 13px;
            transition: color 0.3s;

            &:hover {
                color: $primary-purple;
            }

            .mr-1 {
                margin-right: 4px;
            }
        }
    }
}

.copyright {
    margin-top: 40px;
    color: #9ca3af;
    font-size: 12px;
    text-align: center;
}
</style>