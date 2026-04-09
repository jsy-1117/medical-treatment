<template>
    <div class="page-container">
        <div class="page-header">
            <div class="header-left">
                <h2 class="page-title">患者用户管理</h2>
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item>首页</el-breadcrumb-item>
                    <el-breadcrumb-item>人员管理</el-breadcrumb-item>
                    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
            <div class="header-right">
                <el-button icon="Back" plain @click="goHome">返回门户</el-button>
            </div>
        </div>

        <div class="main-content">
            <el-card class="search-card" shadow="never">
                <el-form :model="queryForm" inline class="search-form">
                    <el-form-item label="用户名">
                        <el-input v-model="queryForm.username" placeholder="输入用户名" clearable prefix-icon="User" />
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-input v-model="queryForm.name" placeholder="输入真实姓名" clearable prefix-icon="Postcard" />
                    </el-form-item>
                    <el-form-item label="手机号">
                        <el-input v-model="queryForm.phone" placeholder="输入手机号" clearable prefix-icon="Iphone" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                        <el-button icon="Refresh" @click="handleReset">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <el-card class="table-card" shadow="never">
                <div class="table-toolbar">
                    <div class="left-tools">
                        <el-icon>
                            <List />
                        </el-icon>
                        <span>用户列表 <span class="text-gray">({{ total }}条)</span></span>
                    </div>
                    <div class="right-tools">
                        <el-button type="primary" icon="Plus" @click="handleAdd">新增用户</el-button>
                    </div>
                </div>

                <el-table :data="tableData" v-loading="loading" stripe style="width: 100%"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
                    <el-table-column prop="id" label="ID" width="80" align="center" />

                    <el-table-column label="用户账号" min-width="180">
                        <template #default="{ row }">
                            <div class="user-info-cell">
                                <el-avatar :size="32" class="user-avatar" icon="UserFilled">
                                    {{ row.name ? row.name.substring(0, 1) : '' }}
                                </el-avatar>
                                <div class="text-col">
                                    <div class="username">{{ row.username }}</div>
                                    <div class="realname">{{ row.name }}</div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="性别" width="100" align="center">
                        <template #default="{ row }">
                            <el-tag :type="row.gender === 1 ? 'primary' : 'danger'" effect="plain" round
                                class="gender-tag">
                                <el-icon>
                                    <component :is="row.gender === 1 ? 'Male' : 'Female'" />
                                </el-icon>
                                {{ row.gender === 1 ? '男' : '女' }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="age" label="年龄" width="80" align="center" />

                    <el-table-column prop="phone" label="手机号" width="140">
                        <template #default="{ row }">
                            <div class="mono-font">{{ row.phone }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="idCard" label="身份证号" width="180">
                        <template #default="{ row }">
                            <span class="text-gray">{{ row.idCard || '-' }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="注册时间" width="180" sortable />

                    <el-table-column label="操作" fixed="right" width="160" align="center">
                        <template #default="{ row }">
                            <el-button type="primary" link size="small" icon="Edit" @click="handleEdit(row)">
                                编辑
                            </el-button>
                            <el-button type="danger" link size="small" icon="Delete" @click="handleDelete(row)">
                                删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="pagination-wrapper">
                    <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
                        :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                        background @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                </div>
            </el-card>
        </div>

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close align-center>
            <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" class="custom-dialog-form">
                <el-form-item label="用户名" prop="username" v-if="!isEdit">
                    <el-input v-model="formData.username" placeholder="设置登录账号" prefix-icon="User" />
                </el-form-item>

                <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
                    <el-input v-model="formData.password" type="password" :placeholder="isEdit ? '不修改请留空' : '设置登录密码'"
                        show-password prefix-icon="Lock" />
                </el-form-item>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="姓名" prop="name">
                            <el-input v-model="formData.name" placeholder="真实姓名" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="性别" prop="gender">
                            <el-radio-group v-model="formData.gender">
                                <el-radio :value="1">男</el-radio>
                                <el-radio :value="2">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="年龄" prop="age">
                            <el-input-number v-model="formData.age" :min="0" :max="120" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="formData.phone" placeholder="11位手机号" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="身份证号" prop="idCard">
                    <el-input v-model="formData.idCard" placeholder="18位身份证号码" prefix-icon="Postcard" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
                    确 定
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { patientApi } from '@/api/patient';
import type { PatientVO, PatientQueryDTO, PatientCreateDTO, PatientUpdateDTO } from '@/types/patient';
// 引入图标
import {
    Search, Refresh, Plus, Edit, Delete, User, Postcard,
    Iphone, Back, List, UserFilled, Male, Female, Lock
} from '@element-plus/icons-vue';

const router = useRouter();

// 表格数据
const tableData = ref<PatientVO[]>([]);
const loading = ref(false);
const total = ref(0);

// 查询表单
const queryForm = reactive<PatientQueryDTO>({
    username: '',
    name: '',
    phone: '',
    pageNum: 1,
    pageSize: 10
});

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref('新增用户');
const isEdit = ref(false);
const editId = ref<number | null>(null);
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

// 表单数据
const formData = reactive<PatientCreateDTO & { id?: number }>({
    username: '',
    password: '',
    name: '',
    gender: 1,
    age: undefined,
    phone: '',
    idCard: ''
});

// 表单验证规则
const formRules: FormRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { max: 50, message: '用户名长度不能超过50个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
    ],
    phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ]
};

// 获取列表数据
const fetchData = async () => {
    loading.value = true;
    try {
        const res = await patientApi.getList(queryForm);
        if (res.code === 200) {
            tableData.value = res.data.records;
            total.value = res.data.total;
        }
    } catch (error) {
        console.error('获取患者列表失败', error);
    } finally {
        loading.value = false;
    }
};

// 搜索
const handleSearch = () => {
    queryForm.pageNum = 1;
    fetchData();
};

// 重置
const handleReset = () => {
    queryForm.username = '';
    queryForm.name = '';
    queryForm.phone = '';
    queryForm.pageNum = 1;
    handleSearch();
};

// 分页改变
const handleSizeChange = (size: number) => {
    queryForm.pageSize = size;
    fetchData();
};

const handleCurrentChange = (page: number) => {
    queryForm.pageNum = page;
    fetchData();
};

// 新增
const handleAdd = () => {
    isEdit.value = false;
    dialogTitle.value = '新增用户';
    editId.value = null;
    resetForm();
    dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: PatientVO) => {
    isEdit.value = true;
    dialogTitle.value = '编辑用户';
    editId.value = row.id;
    Object.assign(formData, {
        username: row.username,
        password: '',
        name: row.name,
        gender: row.gender,
        age: row.age,
        phone: row.phone,
        idCard: row.idCard
    });
    dialogVisible.value = true;
};

// 删除
const handleDelete = (row: PatientVO) => {
    ElMessageBox.confirm(
        `确定要删除用户 "${row.name}" 吗？此操作不可恢复。`,
        '删除警告',
        {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await patientApi.delete(row.id);
            ElMessage.success('删除成功');
            fetchData();
        } catch (error) {
            console.error('删除失败', error);
        }
    });
};

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return;

    await formRef.value.validate(async (valid) => {
        if (!valid) return;

        submitLoading.value = true;
        try {
            if (isEdit.value && editId.value) {
                // 编辑
                const updateData: PatientUpdateDTO = {
                    name: formData.name,
                    gender: formData.gender,
                    age: formData.age,
                    phone: formData.phone,
                    idCard: formData.idCard
                };
                if (formData.password) {
                    updateData.password = formData.password;
                }
                await patientApi.update(editId.value, updateData);
                ElMessage.success('更新成功');
            } else {
                // 新增
                await patientApi.create(formData as PatientCreateDTO);
                ElMessage.success('创建成功');
            }
            dialogVisible.value = false;
            fetchData();
        } catch (error) {
            console.error('提交失败', error);
        } finally {
            submitLoading.value = false;
        }
    });
};

// 重置表单
const resetForm = () => {
    Object.assign(formData, {
        username: '',
        password: '',
        name: '',
        gender: 1,
        age: undefined,
        phone: '',
        idCard: ''
    });
    formRef.value?.resetFields();
};

// 返回首页
const goHome = () => {
    router.push('/home');
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped lang="scss">
.page-container {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: 100vh;
    font-family: 'Inter', sans-serif;
}

/* 1. Page Header */
.page-header {
    background: #fff;
    padding: 20px 24px;
    margin: -20px -20px 20px -20px;
    /* 抵消父级 padding */
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
        .page-title {
            margin: 0 0 8px 0;
            font-size: 20px;
            font-weight: 600;
            color: #111827;
        }
    }
}

.main-content {
    max-width: 100%;
}

/* 2. 搜索栏 */
.search-card {
    margin-bottom: 16px;
    border: none;
    border-radius: 8px;

    .search-form {
        :deep(.el-form-item) {
            margin-bottom: 0;
            margin-right: 24px;
        }
    }
}

/* 3. 表格区域 */
.table-card {
    border: none;
    border-radius: 8px;

    .table-toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .left-tools {
            font-size: 16px;
            font-weight: 600;
            color: #374151;
            display: flex;
            align-items: center;
            gap: 8px;

            .text-gray {
                font-weight: 400;
                color: #9ca3af;
                font-size: 14px;
            }
        }
    }
}

/* 表格内部样式 */
.user-info-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-avatar {
        background: #e0e7ff;
        color: #4f46e5;
    }

    .text-col {
        display: flex;
        flex-direction: column;

        .username {
            font-weight: 600;
            color: #1f2937;
            font-size: 14px;
        }

        .realname {
            font-size: 12px;
            color: #6b7280;
        }
    }
}

.gender-tag {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    width: 60px;
}

.mono-font {
    font-family: 'Roboto Mono', monospace;
    color: #4b5563;
}

.text-gray {
    color: #9ca3af;
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
}

/* 对话框微调 */
.custom-dialog-form {
    padding: 0 10px;
}
</style>