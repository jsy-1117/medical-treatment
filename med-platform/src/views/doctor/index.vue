<template>
    <div class="page-container">
        <div class="page-header">
            <div class="header-left">
                <h2 class="page-title">医生信息管理</h2>
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item>首页</el-breadcrumb-item>
                    <el-breadcrumb-item>人员管理</el-breadcrumb-item>
                    <el-breadcrumb-item>医生管理</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
            <div class="header-right">
                <el-button icon="Back" plain @click="goHome">返回门户</el-button>
            </div>
        </div>

        <div class="main-content">
            <el-card class="search-card" shadow="never">
                <el-form :model="queryForm" inline class="search-form">
                    <el-form-item label="姓名">
                        <el-input v-model="queryForm.name" placeholder="输入医生姓名" clearable prefix-icon="User" />
                    </el-form-item>
                    <el-form-item label="职称">
                        <el-select v-model="queryForm.jobTitle" placeholder="选择职称" clearable style="width: 180px">
                            <el-option label="主任医师" value="主任医师" />
                            <el-option label="副主任医师" value="副主任医师" />
                            <el-option label="主治医师" value="主治医师" />
                            <el-option label="住院医师" value="住院医师" />
                        </el-select>
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
                            <FirstAidKit />
                        </el-icon>
                        <span>医生名录 <span class="text-gray">({{ total }}位)</span></span>
                    </div>
                    <div class="right-tools">
                        <el-button type="primary" icon="Plus" @click="handleAdd">新增医生</el-button>
                    </div>
                </div>

                <el-table :data="tableData" v-loading="loading" stripe style="width: 100%"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
                    <el-table-column prop="id" label="ID" width="70" align="center" />

                    <el-table-column label="医生信息" min-width="200">
                        <template #default="{ row }">
                            <div class="doctor-profile-cell">
                                <el-avatar :size="40" class="avatar" src="" icon="UserFilled">
                                    {{ row.name ? row.name.substring(0, 1) : '' }}
                                </el-avatar>
                                <div class="info">
                                    <div class="name-row">
                                        <span class="name">{{ row.name }}</span>
                                        <span class="username">@{{ row.username }}</span>
                                    </div>
                                    <div class="dept-row">
                                        <el-tag size="small" type="info" effect="plain" round>
                                            {{ getDeptName(row.deptId) }}
                                        </el-tag>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="职称" width="120">
                        <template #default="{ row }">
                            <el-tag :type="getJobTitleTag(row.jobTitle)" effect="light">
                                {{ row.jobTitle }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="phone" label="联系电话" width="140">
                        <template #default="{ row }">
                            <div class="mono-font"><el-icon>
                                    <Phone />
                                </el-icon> {{ row.phone }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="introduction" label="个人简介" min-width="200" show-overflow-tooltip>
                        <template #default="{ row }">
                            <span class="text-gray">{{ row.introduction || '暂无简介' }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="入职时间" width="160" sortable />

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

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close align-center>
            <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px" class="custom-dialog-form">
                <div class="form-section-title">账号信息</div>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户名" prop="username" v-if="!isEdit">
                            <el-input v-model="formData.username" placeholder="登录账号" prefix-icon="User" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="isEdit ? 24 : 12">
                        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
                            <el-input v-model="formData.password" type="password"
                                :placeholder="isEdit ? '留空则不修改' : '设置密码'" show-password prefix-icon="Lock" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-divider border-style="dashed" style="margin: 10px 0 20px;" />

                <div class="form-section-title">职业档案</div>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="姓名" prop="name">
                            <el-input v-model="formData.name" placeholder="医生姓名" prefix-icon="Postcard" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属科室" prop="deptId">
                            <el-select v-model="formData.deptId" placeholder="选择科室" style="width: 100%">
                                <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName"
                                    :value="dept.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="职称" prop="jobTitle">
                            <el-select v-model="formData.jobTitle" placeholder="选择职称" style="width: 100%">
                                <el-option label="主任医师" value="主任医师" />
                                <el-option label="副主任医师" value="副主任医师" />
                                <el-option label="主治医师" value="主治医师" />
                                <el-option label="住院医师" value="住院医师" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话" prop="phone">
                            <el-input v-model="formData.phone" placeholder="工作电话" prefix-icon="Phone" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="医生简介" prop="introduction">
                    <el-input v-model="formData.introduction" type="textarea" :rows="3"
                        placeholder="请输入医生的专业擅长、从业经历等..." maxlength="200" show-word-limit />
                </el-form-item>
            </el-form>

            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保 存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { doctorApi } from '@/api/doctor';
import type { DoctorVO, DoctorQueryDTO, DoctorCreateDTO, DoctorUpdateDTO } from '@/types/doctor';
import { departmentApi } from '@/api/department';
import type { DepartmentVO } from '@/types/department';
// 引入图标
import {
    Search, Refresh, Plus, Edit, Delete, User, Lock, Postcard,
    Phone, Back, FirstAidKit, UserFilled
} from '@element-plus/icons-vue';

const router = useRouter();

// 列表数据
const tableData = ref<DoctorVO[]>([]);
const loading = ref(false);
const total = ref(0);
const departmentList = ref<DepartmentVO[]>([]);

// 查询表单
const queryForm = reactive<DoctorQueryDTO>({
    name: '',
    jobTitle: '',
    pageNum: 1,
    pageSize: 10
});

// 对话框相关
const dialogVisible = ref(false);
const dialogTitle = ref('新增医生');
const isEdit = ref(false);
const editId = ref<number | null>(null);
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

const formData = reactive<DoctorCreateDTO & { id?: number }>({
    deptId: undefined as unknown as number,
    username: '',
    password: '',
    name: '',
    jobTitle: '',
    introduction: '',
    phone: ''
});

// 验证规则
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
    deptId: [
        { required: true, message: '请选择所属科室', trigger: 'change' }
    ],
    jobTitle: [
        { required: true, message: '请选择职称', trigger: 'change' }
    ],
    phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ]
};

// 获取科室名称 (辅助展示)
const getDeptName = (deptId: number) => {
    const dept = departmentList.value.find(d => d.id === deptId);
    return dept ? dept.deptName : `科室${deptId}`;
};

// 获取职称标签颜色
const getJobTitleTag = (title: string) => {
    if (title.includes('主任')) return 'danger'; // 高级职称用红色/橙色
    if (title.includes('主治')) return 'primary'; // 中级用蓝色
    return 'success'; // 初级用绿色
};

// API 操作
const fetchData = async () => {
    loading.value = true;
    try {
        const res = await doctorApi.getList(queryForm);
        if (res.code === 200) {
            tableData.value = res.data.records;
            total.value = res.data.total;
        }
    } catch (error) {
        console.error('获取医生列表失败', error);
    } finally {
        loading.value = false;
    }
};

const fetchDepartments = async () => {
    try {
        const res = await departmentApi.getAll();
        if (res.code === 200) {
            departmentList.value = res.data;
        }
    } catch (error) {
        console.error('获取科室列表失败', error);
    }
};

const handleSearch = () => {
    queryForm.pageNum = 1;
    fetchData();
};

const handleReset = () => {
    queryForm.name = '';
    queryForm.jobTitle = '';
    queryForm.pageNum = 1;
    handleSearch();
};

const handleSizeChange = (size: number) => {
    queryForm.pageSize = size;
    fetchData();
};

const handleCurrentChange = (page: number) => {
    queryForm.pageNum = page;
    fetchData();
};

const handleAdd = () => {
    isEdit.value = false;
    dialogTitle.value = '新增医生';
    editId.value = null;
    resetForm();
    dialogVisible.value = true;
};

const handleEdit = (row: DoctorVO) => {
    isEdit.value = true;
    dialogTitle.value = '编辑医生';
    editId.value = row.id;
    Object.assign(formData, {
        deptId: row.deptId,
        username: row.username,
        password: '',
        name: row.name,
        jobTitle: row.jobTitle,
        introduction: row.introduction,
        phone: row.phone
    });
    dialogVisible.value = true;
};

const handleDelete = (row: DoctorVO) => {
    ElMessageBox.confirm(
        `确定要删除医生 "${row.name}" 吗？此操作将移除该医生的所有排班信息。`,
        '删除警告',
        {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await doctorApi.delete(row.id);
            ElMessage.success('删除成功');
            fetchData();
        } catch (error) {
            console.error('删除失败', error);
        }
    });
};

const handleSubmit = async () => {
    if (!formRef.value) return;

    await formRef.value.validate(async (valid) => {
        if (!valid) return;

        submitLoading.value = true;
        try {
            if (isEdit.value && editId.value) {
                const updateData: DoctorUpdateDTO = {
                    deptId: formData.deptId,
                    name: formData.name,
                    jobTitle: formData.jobTitle,
                    introduction: formData.introduction,
                    phone: formData.phone
                };
                if (formData.password) {
                    updateData.password = formData.password;
                }
                await doctorApi.update(editId.value, updateData);
                ElMessage.success('更新成功');
            } else {
                await doctorApi.create(formData as DoctorCreateDTO);
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

const resetForm = () => {
    Object.assign(formData, {
        deptId: undefined,
        username: '',
        password: '',
        name: '',
        jobTitle: '',
        introduction: '',
        phone: ''
    });
    formRef.value?.resetFields();
};

const goHome = () => {
    router.push('/home');
};

onMounted(() => {
    fetchData();
    fetchDepartments();
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

/* 医生信息聚合列 */
.doctor-profile-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .avatar {
        background: #e0f2fe;
        color: #0284c7;
        font-weight: 600;
        border: 1px solid #bae6fd;
    }

    .info {
        display: flex;
        flex-direction: column;
        gap: 2px;

        .name-row {
            display: flex;
            align-items: baseline;
            gap: 6px;

            .name {
                font-weight: 600;
                color: #1f2937;
                font-size: 14px;
            }

            .username {
                font-size: 12px;
                color: #9ca3af;
            }
        }
    }
}

.mono-font {
    font-family: 'Roboto Mono', monospace;
    color: #4b5563;
    display: flex;
    align-items: center;
    gap: 4px;
}

.text-gray {
    color: #9ca3af;
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
}

/* 对话框美化 */
.custom-dialog-form {
    padding: 0 10px;

    .form-section-title {
        font-size: 14px;
        font-weight: 600;
        color: #1e293b;
        margin-bottom: 16px;
        padding-left: 8px;
        border-left: 3px solid #3b82f6;
    }
}
</style>