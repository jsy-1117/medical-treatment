<template>
    <div class="register-container">
        <el-card class="register-card">
            <template #header>
                <div class="card-header">
                    <h2>智慧医疗平台 - 管理员注册</h2>
                </div>
            </template>

            <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="registerForm.username" placeholder="请输入用户名" clearable />
                </el-form-item>

                <el-form-item label="密码" prop="password">
                    <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password
                        clearable />
                </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password
                        clearable />
                </el-form-item>

                <el-form-item label="姓名" prop="name">
                    <el-input v-model="registerForm.name" placeholder="请输入姓名" clearable />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" :loading="loading" style="width: 100%" @click="handleRegister">
                        注册
                    </el-button>
                </el-form-item>

                <el-form-item>
                    <el-button type="text" @click="$router.push('/login')">
                        已有账号？立即登录
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { adminApi } from '@/api/admin';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

const router = useRouter();

// 表单引用
const registerFormRef = ref<FormInstance>();
const loading = ref(false);

// 表单数据
const registerForm = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    name: ''
});

// 确认密码验证
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请再次输入密码'));
    } else if (value !== registerForm.password) {
        callback(new Error('两次输入密码不一致'));
    } else {
        callback();
    }
};

// 表单验证规则
const registerRules: FormRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { max: 50, message: '姓名长度不能超过 50 个字符', trigger: 'blur' }
    ]
};

// 处理注册
const handleRegister = async () => {
    if (!registerFormRef.value) return;

    await registerFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true;
            try {
                await adminApi.register({
                    username: registerForm.username,
                    password: registerForm.password,
                    name: registerForm.name
                });
                ElMessage.success('注册成功，请登录');
                router.push('/login');
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
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

    .register-card {
        width: 450px;

        .card-header {
            text-align: center;

            h2 {
                margin: 0;
                color: #303133;
            }
        }
    }
}
</style>