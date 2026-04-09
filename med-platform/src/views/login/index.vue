<template>
    <div class="admin-login-wrapper">
        <div class="login-content">
            <div class="login-header">
                <div class="logo-circle">
                    <el-icon :size="32">
                        <Monitor />
                    </el-icon>
                </div>
                <div class="header-text">
                    <h1 class="system-name">智慧医疗管理后台</h1>
                    <p class="system-sub">Smart Medical Administration</p>
                </div>
            </div>

            <el-card class="login-card" shadow="never">
                <div class="card-intro">
                    <span class="role-badge">管理员入口</span>
                    <p>请输入您的管理账号进行操作</p>
                </div>

                <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" class="login-form"
                    @submit.prevent>
                    <el-form-item prop="username">
                        <el-input v-model="loginForm.username" placeholder="管理员账号" :prefix-icon="User" clearable
                            tabindex="1" />
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input v-model="loginForm.password" type="password" placeholder="管理密码" :prefix-icon="Lock"
                            show-password clearable tabindex="2" @keyup.enter="handleLogin" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" :loading="loading" class="submit-btn" auto-insert-space
                            @click="handleLogin">
                            登 录
                        </el-button>
                    </el-form-item>

                    <div class="form-footer">
                        <el-button link class="footer-link" @click="$router.push('/')">
                            <el-icon class="el-icon--left">
                                <Back />
                            </el-icon> 返回门户首页
                        </el-button>
                        <el-button link class="footer-link" @click="$router.push('/register')">
                            注册新账号
                        </el-button>
                    </div>
                </el-form>
            </el-card>

            <div class="copyright">
                &copy; 2024 Smart Medical System · Admin Console
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useAdminStore } from '@/stores/admin';
import type { FormInstance, FormRules } from 'element-plus';
import type { AdminLoginDTO } from '@/types/admin';
// 引入图标
import { User, Lock, Monitor, Back } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const adminStore = useAdminStore();
const router = useRouter();

// 表单引用
const loginFormRef = ref<FormInstance>();
const loading = ref(false);

// 表单数据
const loginForm = reactive<AdminLoginDTO>({
    username: '',
    password: ''
});

// 表单验证规则
const loginRules: FormRules = {
    username: [
        { required: true, message: '请输入管理员账号', trigger: 'blur' },
        { min: 3, max: 50, message: '账号长度在 3 到 50 个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ]
};

// 处理登录
// 处理登录（推荐方案）
const handleLogin = async () => {
    if (!loginFormRef.value) return;

    await loginFormRef.value.validate(async (valid) => {
        if (!valid) return;

        loading.value = true;
        try {
            await adminStore.login(loginForm);
            // 如果 store 中的跳转失败，这里作为备用跳转
            if (router.currentRoute.value.path === '/login') {
                router.push('/admin/home');
            }
        } catch (error) {
            console.error('登录失败', error);
        } finally {
            loading.value = false;
        }
    });
};
</script>

<style scoped lang="scss">
.admin-login-wrapper {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    /* 背景：深色系，体现后台管理的严肃感 */
    background: #0f172a;
    background-image: radial-gradient(at 0% 0%, rgba(59, 130, 246, 0.15) 0px, transparent 50%),
        radial-gradient(at 100% 100%, rgba(6, 182, 212, 0.15) 0px, transparent 50%);
    font-family: 'Inter', sans-serif;
}

.login-content {
    width: 100%;
    max-width: 400px;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

/* 头部品牌 */
.login-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 32px;
    text-align: center;

    .logo-circle {
        width: 64px;
        height: 64px;
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        box-shadow: 0 10px 25px -5px rgba(37, 99, 235, 0.5);
        margin-bottom: 20px;
    }

    .header-text {
        color: white;

        .system-name {
            font-size: 24px;
            font-weight: 700;
            margin: 0 0 4px;
            letter-spacing: 0.5px;
        }

        .system-sub {
            color: #94a3b8;
            font-size: 13px;
            margin: 0;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
    }
}

/* 卡片样式 */
.login-card {
    width: 100%;
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(255, 255, 255, 0.05);
    /* 玻璃拟态微弱效果，或纯色 */
    background: #ffffff;
    /* 保持高对比度，还是用白色卡片更清晰 */
    border-radius: 16px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    overflow: visible;
    /* 允许阴影溢出 */

    :deep(.el-card__body) {
        padding: 40px 32px;
    }

    .card-intro {
        text-align: center;
        margin-bottom: 30px;

        .role-badge {
            background: #eff6ff;
            color: #3b82f6;
            font-size: 12px;
            font-weight: 600;
            padding: 4px 12px;
            border-radius: 20px;
            display: inline-block;
            margin-bottom: 12px;
        }

        p {
            color: #64748b;
            font-size: 14px;
            margin: 0;
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
        background: #1e293b;
        /* 黑色按钮，呼应深色主题 */
        border-color: #1e293b;
        margin-top: 8px;
        transition: all 0.3s;

        &:hover {
            background: #334155;
            border-color: #334155;
            transform: translateY(-1px);
        }
    }

    .form-footer {
        display: flex;
        justify-content: space-between;
        margin-top: 20px;

        .footer-link {
            color: #64748b;
            font-size: 13px;

            &:hover {
                color: #3b82f6;
            }
        }
    }
}

.copyright {
    margin-top: 30px;
    color: #475569;
    font-size: 12px;
}
</style>