<template>
    <div class="page-container">
        <div class="page-header">
            <div class="header-left">
                <h2 class="page-title">排班计划管理</h2>
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item>首页</el-breadcrumb-item>
                    <el-breadcrumb-item>业务中心</el-breadcrumb-item>
                    <el-breadcrumb-item>排班管理</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
            <div class="header-right">
                <el-button icon="Back" plain @click="goHome">返回门户</el-button>
            </div>
        </div>

        <div class="main-content">
            <el-card class="search-card" shadow="never">
                <el-form :model="queryForm" inline class="search-form">
                    <el-form-item label="所属科室">
                        <el-select v-model="queryForm.deptId" placeholder="全部科室" clearable filterable
                            style="width: 180px" @change="onDeptChange">
                            <template #prefix><el-icon>
                                    <OfficeBuilding />
                                </el-icon></template>
                            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName"
                                :value="dept.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="排班医生">
                        <el-select v-model="queryForm.doctorId" placeholder="全部医生" clearable filterable
                            style="width: 180px">
                            <template #prefix><el-icon>
                                    <User />
                                </el-icon></template>
                            <el-option v-for="doc in filteredDoctorList" :key="doc.id" :label="doc.name"
                                :value="doc.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="日期范围">
                        <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
                            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
                            style="width: 240px" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">查询</el-button>
                        <el-button icon="Refresh" @click="handleReset">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <el-card class="table-card" shadow="never">
                <div class="table-toolbar">
                    <div class="left-tools">
                        <el-icon>
                            <Calendar />
                        </el-icon>
                        <span>排班列表 <span class="text-gray">({{ total }}条)</span></span>
                    </div>
                    <div class="right-tools">
                        <el-button type="primary" icon="Plus" @click="handleAdd">发布新排班</el-button>
                    </div>
                </div>

                <el-table :data="tableData" v-loading="loading" stripe style="width: 100%"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
                    <el-table-column prop="id" label="ID" width="80" align="center" fixed="left" />

                    <el-table-column label="出诊日期" width="160" sortable prop="workDate">
                        <template #default="{ row }">
                            <div class="date-cell">
                                <el-icon>
                                    <Calendar />
                                </el-icon>
                                <span>{{ row.workDate }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="出诊时段" width="120">
                        <template #default="{ row }">
                            <el-tag :type="row.shiftType === 1 ? 'warning' : 'primary'" effect="light"
                                class="shift-tag">
                                <el-icon class="mr-1">
                                    <component :is="row.shiftType === 1 ? 'Sunny' : 'Moon'" />
                                </el-icon>
                                {{ row.shiftType === 1 ? '上午' : '下午' }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column label="医生信息" min-width="180">
                        <template #default="{ row }">
                            <div class="doctor-cell">
                                <el-avatar :size="32" class="avatar" icon="UserFilled">
                                    {{ row.doctorName ? row.doctorName.substring(0, 1) : 'D' }}
                                </el-avatar>
                                <div class="info">
                                    <div class="name">{{ row.doctorName || `医生${row.doctorId}` }}</div>
                                    <div class="dept">{{ row.deptName || `科室${row.deptId}` }}</div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="号源监控 (余/总)" min-width="160">
                        <template #default="{ row }">
                            <div class="quota-info">
                                <span class="nums">
                                    <span :class="getQuotaColor(row.remainingQuota)">{{ row.remainingQuota }}</span>
                                    <span class="divider">/</span>
                                    {{ row.quota }}
                                </span>
                                <el-progress :percentage="Math.round((row.remainingQuota / row.quota) * 100) || 0"
                                    :show-text="false" :stroke-width="4"
                                    :color="getQuotaColorHex(row.remainingQuota)" />
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="plain" round
                                class="status-tag">
                                <span :class="['dot', row.status === 1 ? 'bg-success' : 'bg-danger']"></span>
                                {{ row.status === 1 ? '正常' : '停诊' }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" fixed="right" width="200" align="center">
                        <template #default="{ row }">
                            <el-button v-if="row.status === 1" type="warning" link size="small" icon="VideoPause"
                                @click="handleStop(row)">
                                停诊
                            </el-button>
                            <el-button v-else type="success" link size="small" icon="VideoPlay"
                                @click="handleResume(row)">
                                恢复
                            </el-button>
                            <el-divider direction="vertical" />
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

        <el-dialog v-model="dialogVisible" title="发布新排班" width="550px" destroy-on-close align-center>
            <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" class="custom-dialog-form">
                <el-alert title="发布排班前，请确保该医生当日无其他冲突安排" type="info" show-icon :closable="false" class="mb-4" />

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="选择科室" prop="deptId">
                            <el-select v-model="formData.deptId" placeholder="请选择科室" @change="onFormDeptChange"
                                style="width: 100%">
                                <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName"
                                    :value="dept.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="选择医生" prop="doctorId">
                            <el-select v-model="formData.doctorId" placeholder="请先选择科室" :disabled="!formData.deptId"
                                style="width: 100%">
                                <el-option v-for="doc in formDoctorList" :key="doc.id" :label="doc.name"
                                    :value="doc.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="出诊日期" prop="workDate">
                    <el-date-picker v-model="formData.workDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
                        style="width: 100%" />
                </el-form-item>

                <el-form-item label="出诊时段" prop="shiftType">
                    <el-radio-group v-model="formData.shiftType">
                        <el-radio-button :value="1"><el-icon class="mr-1">
                                <Sunny />
                            </el-icon> 上午</el-radio-button>
                        <el-radio-button :value="2"><el-icon class="mr-1">
                                <Moon />
                            </el-icon> 下午</el-radio-button>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="号源数量" prop="quota">
                    <el-input-number v-model="formData.quota" :min="1" :max="200" controls-position="right"
                        style="width: 100%" />
                    <div class="form-tip">建议值：普通门诊 50，专家门诊 20</div>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitLoading">立即发布</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { scheduleApi, doctorApi } from '@/api/doctor';
import { departmentApi } from '@/api/department';
import type { DoctorScheduleVO, ScheduleQueryDTO, ScheduleCreateDTO, DoctorVO } from '@/types/doctor';
import type { DepartmentVO } from '@/types/department';
// 引入图标
import {
    Search, Refresh, Plus, Calendar, User, OfficeBuilding,
    Sunny, Moon, UserFilled, VideoPause, VideoPlay, Delete, Back, List
} from '@element-plus/icons-vue';

const router = useRouter();

const tableData = ref<DoctorScheduleVO[]>([]);
const loading = ref(false);
const total = ref(0);

const departmentList = ref<DepartmentVO[]>([]);
const doctorList = ref<DoctorVO[]>([]);
const dateRange = ref<[string, string] | null>(null);

const queryForm = reactive<ScheduleQueryDTO>({
    deptId: undefined,
    doctorId: undefined,
    startDate: undefined,
    endDate: undefined,
    pageNum: 1,
    pageSize: 10
});

const dialogVisible = ref(false);
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

const formData = reactive<ScheduleCreateDTO>({
    doctorId: undefined as unknown as number,
    deptId: undefined as unknown as number,
    workDate: '',
    shiftType: 1,
    quota: 20
});

const formDoctorList = ref<DoctorVO[]>([]);

const formRules: FormRules = {
    deptId: [{ required: true, message: '请选择科室', trigger: 'change' }],
    doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
    workDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
    shiftType: [{ required: true, message: '请选择时段', trigger: 'change' }],
    quota: [{ required: true, message: '请输入号源数量', trigger: 'blur' }]
};

// 根据科室筛选医生
const filteredDoctorList = computed(() => {
    if (!queryForm.deptId) return doctorList.value;
    return doctorList.value.filter(d => d.deptId === queryForm.deptId);
});

// 辅助函数：号源颜色
const getQuotaColor = (num: number) => {
    if (num === 0) return 'text-danger';
    if (num < 10) return 'text-warning';
    return 'text-success';
};
const getQuotaColorHex = (num: number) => {
    if (num === 0) return '#f56c6c';
    if (num < 10) return '#e6a23c';
    return '#67c23a';
};

// 获取科室列表
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

// 获取医生列表
const fetchDoctors = async () => {
    try {
        const res = await doctorApi.getList({ pageNum: 1, pageSize: 1000 });
        if (res.code === 200) {
            doctorList.value = res.data.records;
        }
    } catch (error) {
        console.error('获取医生列表失败', error);
    }
};

// 获取排班列表
// 获取排班列表
const fetchData = async () => {
    loading.value = true;
    if (dateRange.value) {
        queryForm.startDate = dateRange.value[0];
        queryForm.endDate = dateRange.value[1];
    } else {
        queryForm.startDate = undefined;
        queryForm.endDate = undefined;
    }
    try {
        const res = await scheduleApi.getList(queryForm);
        if (res.code === 200) {
            // 关联医生和科室信息
            tableData.value = res.data.records.map(schedule => {
                // 查找医生信息
                const doctor = doctorList.value.find(d => d.id === schedule.doctorId);
                // 查找科室信息
                const dept = departmentList.value.find(d => d.id === schedule.deptId);

                return {
                    ...schedule,
                    doctorName: doctor?.name || `医生${schedule.doctorId}`,
                    deptName: dept?.deptName || `科室${schedule.deptId}`
                };
            });
            total.value = res.data.total;
        }
    } catch (error) {
        console.error('获取排班列表失败', error);
    } finally {
        loading.value = false;
    }
};

const onDeptChange = () => {
    queryForm.doctorId = undefined;
};

const onFormDeptChange = () => {
    formData.doctorId = undefined as unknown as number;
    formDoctorList.value = doctorList.value.filter(d => d.deptId === formData.deptId);
};

const handleSearch = () => {
    queryForm.pageNum = 1;
    fetchData();
};

const handleReset = () => {
    queryForm.deptId = undefined;
    queryForm.doctorId = undefined;
    dateRange.value = null;
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
    Object.assign(formData, {
        doctorId: undefined,
        deptId: undefined,
        workDate: '',
        shiftType: 1,
        quota: 20
    });
    formDoctorList.value = [];
    dialogVisible.value = true;
};

const handleSubmit = async () => {
    if (!formRef.value) return;

    await formRef.value.validate(async (valid) => {
        if (!valid) return;

        submitLoading.value = true;
        try {
            await scheduleApi.create(formData);
            ElMessage.success('发布成功');
            dialogVisible.value = false;
            fetchData();
        } catch (error) {
            console.error('发布失败', error);
        } finally {
            submitLoading.value = false;
        }
    });
};

const handleStop = (row: DoctorScheduleVO) => {
    ElMessageBox.confirm('确定停诊该排班吗？患者将无法预约。', '停诊警告', {
        confirmButtonText: '确定停诊',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await scheduleApi.updateStatus(row.id, 0);
            ElMessage.success('操作成功');
            fetchData();
        } catch (error) {
            console.error('操作失败', error);
        }
    });
};

const handleResume = (row: DoctorScheduleVO) => {
    ElMessageBox.confirm('确定恢复该排班吗？', '提示', { type: 'info' }).then(async () => {
        try {
            await scheduleApi.updateStatus(row.id, 1);
            ElMessage.success('操作成功');
            fetchData();
        } catch (error) {
            console.error('操作失败', error);
        }
    });
};

const handleDelete = (row: DoctorScheduleVO) => {
    ElMessageBox.confirm('确定删除该排班吗？此操作不可恢复。', '删除警告', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
    }).then(async () => {
        try {
            await scheduleApi.delete(row.id);
            ElMessage.success('删除成功');
            fetchData();
        } catch (error) {
            console.error('删除失败', error);
        }
    });
};

const goHome = () => {
    router.push('/admin/home');
};

onMounted(() => {
    fetchDepartments();
    fetchDoctors();
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

/* 表格内容美化 */
.date-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Roboto Mono', monospace;
    color: #374151;
}

.doctor-cell {
    display: flex;
    align-items: center;
    gap: 10px;

    .avatar {
        background: #e0f2fe;
        color: #0284c7;
    }

    .info {
        display: flex;
        flex-direction: column;

        .name {
            font-weight: 500;
            color: #1f2937;
            font-size: 14px;
        }

        .dept {
            font-size: 12px;
            color: #6b7280;
        }
    }
}

.shift-tag {
    display: flex;
    align-items: center;
    width: fit-content;

    .mr-1 {
        margin-right: 4px;
    }
}

.quota-info {
    display: flex;
    flex-direction: column;
    justify-content: center;
    max-width: 120px;

    .nums {
        font-size: 13px;
        margin-bottom: 2px;

        .divider {
            color: #d1d5db;
        }

        .text-success {
            color: #67c23a;
            font-weight: 600;
        }

        .text-warning {
            color: #e6a23c;
            font-weight: 600;
        }

        .text-danger {
            color: #f56c6c;
            font-weight: 600;
        }
    }
}

.status-tag {
    display: flex;
    align-items: center;
    gap: 6px;

    .dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;

        &.bg-success {
            background: #67c23a;
        }

        &.bg-danger {
            background: #f56c6c;
        }
    }
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
}

/* 对话框 */
.custom-dialog-form {
    padding: 0 10px;

    .mb-4 {
        margin-bottom: 20px;
    }

    .mr-1 {
        margin-right: 4px;
    }

    .form-tip {
        font-size: 12px;
        color: #9ca3af;
        margin-top: 4px;
    }
}
</style>