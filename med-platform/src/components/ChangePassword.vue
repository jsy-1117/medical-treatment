<template>
    <el-dialog v-model="visible" title="修改密码" width="420px" @close="handleClose" align-center>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="password-form">
            <el-form-item label="旧密码" prop="oldPassword">
                <el-input v-model="form.oldPassword" type="password" placeholder="请输入旧密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="handleClose">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="loading">确认修改</el-button>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

const props = defineProps<{
    modelValue: boolean;
    onSubmit: (data: { oldPassword: string; newPassword: string }) => Promise<void>;
}>();

const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
}>();

const visible = ref(props.modelValue);
const loading = ref(false);
const formRef = ref<FormInstance>();

const form = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
});

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
    if (value !== form.newPassword) {
        callback(new Error('两次输入的密码不一致'));
    } else {
        callback();
    }
};

const rules: FormRules = {
    oldPassword: [
        { required: true, message: '请输入旧密码', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
    ]
};

watch(() => props.modelValue, (val) => {
    visible.value = val;
});

watch(visible, (val) => {
    emit('update:modelValue', val);
});

const handleClose = () => {
    visible.value = false;
    form.oldPassword = '';
    form.newPassword = '';
    form.confirmPassword = '';
    formRef.value?.resetFields();
};

const handleSubmit = async () => {
    if (!formRef.value) return;

    await formRef.value.validate(async (valid) => {
        if (!valid) return;

        loading.value = true;
        try {
            await props.onSubmit({
                oldPassword: form.oldPassword,
                newPassword: form.newPassword
            });
            ElMessage.success('密码修改成功，请重新登录');
            handleClose();
        } catch (error) {
            console.error('修改失败', error);
        } finally {
            loading.value = false;
        }
    });
};
</script>

<style scoped lang="scss">
.password-form {
    :deep(.el-form-item__label) {
        color: #64748b;
    }
}
</style>