<template>
    <div class="page-container">
        <div class="page-header">
            <div class="header-left">
                <h2 class="page-title">科室管理</h2>
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item>首页</el-breadcrumb-item>
                    <el-breadcrumb-item>医院管理</el-breadcrumb-item>
                    <el-breadcrumb-item>科室管理</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
            <div class="header-right">
                <el-button icon="Back" plain @click="goHome">返回门户</el-button>
            </div>
        </div>

        <div class="main-content">
            <el-card class="search-card" shadow="never">
                <el-form :model="queryForm" inline class="search-form">
                    <el-form-item label="科室名称">
                        <el-input v-model="queryForm.deptName" placeholder="输入科室名称" clearable prefix-icon="Search" />
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
                        <span>科室列表 <span class="text-gray">({{ total }}个)</span></span>
                    </div>
                    <div class="right-tools">
                        <el-button type="primary" icon="Plus" @click="handleAdd">新增科室</el-button>
                    </div>
                </div>

                <el-table :data="tableData" v-loading="loading" stripe style="width: 100%"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
                    <el-table-column prop="id" label="ID" width="80" align="center" />

                    <el-table-column prop="deptName" label="科室名称" width="180">
                        <template #default="{ row }">
                            <div class="dept-name-cell">
                                <div class="icon-box">
                                    <el-icon>
                                        <OfficeBuilding />
                                    </el-icon>
                                </div>
                                <span class="name">{{ row.deptName }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="location" label="位置" width="200">
                        <template #default="{ row }">
                            <div class="location-cell">
                                <el-icon class="loc-icon">
                                    <Location />
                                </el-icon>
                                <span>{{ row.location || '未设置' }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="deptDesc" label="科室描述" min-width="250" show-overflow-tooltip>
                        <template #default="{ row }">
                            <span class="text-gray">{{ row.deptDesc || '暂无描述' }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="创建时间" width="180" sortable />

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

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close align-center>
            <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px" class="custom-dialog-form">
                <el-form-item label="科室名称" prop="deptName">
                    <el-input v-model="formData.deptName" placeholder="例如：心血管内科" prefix-icon="OfficeBuilding" />
                </el-form-item>

                <el-form-item label="科室位置" prop="location">
                    <el-input v-model="formData.location" placeholder="例如：门诊楼3层302" prefix-icon="Location" />
                </el-form-item>

                <el-form-item label="科室描述" prop="deptDesc">
                    <el-input v-model="formData.deptDesc" type="textarea" :rows="4" placeholder="请输入该科室的职能描述..."
                        maxlength="255" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { departmentApi } from '@/api/department';
import type { DepartmentVO, DepartmentQueryDTO, DepartmentCreateDTO, DepartmentUpdateDTO } from '@/types/department';
// 引入图标
import {
    Search, Refresh, Plus, Edit, Delete, Back, List,
    OfficeBuilding, Location
} from '@element-plus/icons-vue';

const router = useRouter();

const tableData = ref<DepartmentVO[]>([]);
const loading = ref(false);
const total = ref(0);

const queryForm = reactive<DepartmentQueryDTO>({
    deptName: '',
    pageNum: 1,
    pageSize: 10
});

const dialogVisible = ref(false);
const dialogTitle = ref('新增科室');
const isEdit = ref(false);
const editId = ref<number | null>(null);
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

const formData = reactive<DepartmentCreateDTO>({
    deptName: '',
    deptDesc: '',
    location: ''
});

const formRules: FormRules = {
    deptName: [
        { required: true, message: '请输入科室名称', trigger: 'blur' },
        { max: 50, message: '科室名称长度不能超过50个字符', trigger: 'blur' }
    ],
    deptDesc: [
        { max: 255, message: '科室描述长度不能超过255个字符', trigger: 'blur' }
    ],
    location: [
        { max: 100, message: '科室位置长度不能超过100个字符', trigger: 'blur' }
    ]
};

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await departmentApi.getList(queryForm);
        if (res.code === 200) {
            tableData.value = res.data.records;
            total.value = res.data.total;
        }
    } catch (error) {
        console.error('获取科室列表失败', error);
    } finally {
        loading.value = false;
    }
};

const handleSearch = () => {
    queryForm.pageNum = 1;
    fetchData();
};

const handleReset = () => {
    queryForm.deptName = '';
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
    dialogTitle.value = '新增科室';
    editId.value = null;
    resetForm();
    dialogVisible.value = true;
};

const handleEdit = (row: DepartmentVO) => {
    isEdit.value = true;
    dialogTitle.value = '编辑科室';
    editId.value = row.id;
    Object.assign(formData, {
        deptName: row.deptName,
        deptDesc: row.deptDesc,
        location: row.location
    });
    dialogVisible.value = true;
};

const handleDelete = (row: DepartmentVO) => {
    ElMessageBox.confirm(
        `确定要删除科室 "${row.deptName}" 吗？删除后可能会影响关联的医生排班。`,
        '删除警告',
        {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await departmentApi.delete(row.id);
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
                const updateData: DepartmentUpdateDTO = {
                    deptName: formData.deptName,
                    deptDesc: formData.deptDesc,
                    location: formData.location
                };
                await departmentApi.update(editId.value, updateData);
                ElMessage.success('更新成功');
            } else {
                await departmentApi.create(formData as DepartmentCreateDTO);
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
        deptName: '',
        deptDesc: '',
        location: ''
    });
    formRef.value?.resetFields();
};

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

/* 4. 表格内容美化 */
.dept-name-cell {
    display: flex;
    align-items: center;
    gap: 10px;

    .icon-box {
        width: 32px;
        height: 32px;
        background: #f0f9ff;
        color: #0ea5e9;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .name {
        font-weight: 600;
        color: #1e293b;
    }
}

.location-cell {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #4b5563;

    .loc-icon {
        color: #94a3b8;
    }
}

.text-gray {
    color: #9ca3af;
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
}

/* 5. 对话框 */
.custom-dialog-form {
    padding: 0 10px;
}
</style>