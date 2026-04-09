<template>
    <div class="profile-container">
        <el-card class="profile-card">
            <template #header>
                <div class="card-header">
                    <h2>个人中心</h2>
                </div>
            </template>

            <!-- 个人信息展示 -->
            <div class="info-section">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="用户名">
                        {{ adminStore.adminInfo?.username }}
                    </el-descriptions-item>
                    <el-descriptions-item label="姓名">
                        {{ adminStore.adminInfo?.name }}
                    </el-descriptions-item>
                    <el-descriptions-item label="ID">
                        {{ adminStore.adminInfo?.id }}
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">
                        {{ adminStore.adminInfo?.createTime }}
                    </el-descriptions-item>
                </el-descriptions>
            </div>

            <!-- 修改信息表单 -->
            <el-divider>修改信息</el-divider>

            <el-form ref="updateFormRef" :model="updateForm" :rules="updateRules" label-width="100px">
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="updateForm.name" placeholder="请输入姓名" clearable />
                </el-form-item>

                <el-form-item label="新密码" prop="password">
                    <el-input v-model="updateForm.password" type="password" placeholder="不修改请留空" show-password
                        clearable />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" :loading="loading" @click="handleUpdate">
                        保存修改
                    </el-button>
                    <el-button @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { useAdminStore } from '@/stores/admin';
import { adminApi } from '@/api/admin';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

const adminStore = useAdminStore();

// 表单引用
const updateFormRef = ref<FormInstance>();
const loading = ref(false);

// 表单数据
const updateForm = reactive({
    name: '',
    password: ''
});

// 表单验证规则
const updateRules: FormRules = {
    name: [
        { max: 50, message: '姓名长度不能超过 50 个字符', trigger: 'blur' }
    ],
    password: [
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ]
};

// 重置表单
const resetForm = () => {
    updateForm.name = adminStore.adminInfo?.name || '';
    updateForm.password = '';
};

// 处理更新
// 处理更新
const handleUpdate = async () => {
    if (!updateFormRef.value || !adminStore.adminInfo) return;

    await updateFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true;
            try {
                const updateData: any = {};
                if (updateForm.name) updateData.name = updateForm.name;
                if (updateForm.password) updateData.password = updateForm.password;

                if (Object.keys(updateData).length === 0) {
                    ElMessage.warning('请修改至少一项信息');
                    loading.value = false;  // 添加这行
                    return;
                }

                // 添加空值检查
                if (!adminStore.adminInfo) {
                    ElMessage.error('获取用户信息失败');
                    loading.value = false;
                    return;
                }

                await adminApi.updateAdmin(adminStore.adminInfo.id, updateData);
                ElMessage.success('修改成功');

                // 刷新管理员信息
                await adminStore.getCurrentAdmin();
                resetForm();
            } catch (error) {
                console.error('修改失败', error);
            } finally {
                loading.value = false;
            }
        }
    });
};

onMounted(() => {
    resetForm();
});
</script>

<style scoped lang="scss">
.profile-container {
    padding: 20px;

    .profile-card {
        max-width: 800px;
        margin: 0 auto;

        .card-header {
            h2 {
                margin: 0;
                color: #303133;
            }
        }

        .info-section {
            margin-bottom: 20px;
        }
    }
}
</style>