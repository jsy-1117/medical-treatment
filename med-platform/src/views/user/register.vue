<template>
    <div class="register-wrapper">
        <div class="register-container">
            <div class="register-header">
                <div class="brand">
                    <div class="logo-box">
                        <el-icon :size="28">
                            <FirstAidKit />
                        </el-icon>
                    </div>
                    <span class="brand-name">智慧医疗平台</span>
                </div>
                <p class="brand-slogan">创建您的健康账户，开启智慧诊疗之旅</p>
            </div>

            <el-card class="register-card" shadow="hover">
                <div class="card-title">
                    <h3>免费注册</h3>
                    <p>填写以下信息完成账号创建</p>
                </div>

                <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large"
                    class="register-form">
                    <el-form-item prop="username">
                        <el-input v-model="registerForm.username" placeholder="设置用户名 (登录账号)" :prefix-icon="User"
                            clearable />
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input v-model="registerForm.password" type="password" placeholder="设置登录密码 (至少6位)"
                            :prefix-icon="Lock" show-password />
                    </el-form-item>

                    <el-form-item prop="name">
                        <el-input v-model="registerForm.name" placeholder="请输入真实姓名" :prefix-icon="Postcard" clearable />
                    </el-form-item>

                    <el-form-item prop="phone">
                        <el-input v-model="registerForm.phone" placeholder="请输入手机号码" :prefix-icon="Iphone" clearable
                            maxlength="11" />
                    </el-form-item>

                    <el-form-item class="submit-item">
                        <el-button type="primary" :loading="loading" class="submit-btn" round @click="handleRegister">
                            立即注册
                        </el-button>
                    </el-form-item>

                    <div class="form-footer">
                        <span class="text-gray">已有账号？</span>
                        <el-button type="primary" link @click="$router.push('/user/login')">
                            直接登录
                        </el-button>
                        <el-divider direction="vertical" />
                        <el-button link type="info" @click="$router.push('/')">
                            返回首页
                        </el-button>
                    </div>
                </el-form>
            </el-card>

            <div class="register-footer">
                <p>&copy; 2025 Smart Medical Service. All rights reserved.</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { patientAuthApi } from '@/api/patientAuth';
// 引入图标
import { User, Lock, FirstAidKit, Iphone, Postcard } from '@element-plus/icons-vue';

const router = useRouter();

const registerFormRef = ref<FormInstance>();
const loading = ref(false);

const registerForm = reactive({
    username: '',
    password: '',
    name: '',
    phone: ''
});

const registerRules: FormRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ]
};

const handleRegister = async () => {
    if (!registerFormRef.value) return;

    await registerFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true;
            try {
                await patientAuthApi.register(registerForm);
                ElMessage.success('注册成功，请登录');
                router.push('/user/login');
            } catch (error) {
                console.error('注册失败', error);
            } finally {
                loading.value = false;
            }
        }
    });
};
</script>

<style scoped lang="scss">
.register-wrapper {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    /* 背景：与登录页保持一致的柔和渐变 */
    background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
    position: relative;
    overflow: hidden;
    padding: 20px 0;
    /* 防止屏幕过矮时内容贴边 */

    /* 背景装饰 */
    &::before {
        content: '';
        position: absolute;
        bottom: -100px;
        left: -100px;
        width: 400px;
        height: 400px;
        background: radial-gradient(circle, rgba(16, 185, 129, 0.08) 0%, rgba(255, 255, 255, 0) 70%);
        border-radius: 50%;
    }
}

.register-container {
    width: 100%;
    max-width: 440px;
    padding: 20px;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.register-header {
    text-align: center;
    margin-bottom: 25px;

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

.register-card {
    width: 100%;
    border-radius: 16px;
    border: none;
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.01);

    :deep(.el-card__body) {
        padding: 35px 32px;
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

.register-form {

    /* 增加输入框间距，看起来更透气 */
    :deep(.el-form-item) {
        margin-bottom: 22px;
    }

    .submit-item {
        margin-top: 10px;
        margin-bottom: 10px;
    }

    .submit-btn {
        width: 100%;
        padding: 22px 0;
        font-size: 16px;
        font-weight: 600;
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
        margin-top: 10px;
        font-size: 14px;

        .text-gray {
            color: #94a3b8;
        }
    }
}

.register-footer {
    margin-top: 30px;
    text-align: center;
    color: #94a3b8;
    font-size: 12px;
}
</style>