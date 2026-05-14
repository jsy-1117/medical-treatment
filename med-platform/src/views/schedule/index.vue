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
                <el-button type="primary" icon="Plus" @click="handleAdd">发布新排班</el-button>
            </div>
        </div>

        <div class="main-content">
            <el-card class="search-card" shadow="never">
                <el-form :model="queryForm" inline class="search-form">
                    <el-form-item label="所属科室">
                        <el-select
                            v-model="queryForm.deptId"
                            placeholder="全部科室"
                            clearable
                            filterable
                            style="width: 180px"
                            @change="onDeptChange"
                        >
                            <template #prefix>
                                <el-icon><OfficeBuilding /></el-icon>
                            </template>
                            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="排班医生">
                        <el-select v-model="queryForm.doctorId" placeholder="全部医生" clearable filterable style="width: 180px">
                            <template #prefix>
                                <el-icon><User /></el-icon>
                            </template>
                            <el-option v-for="doc in filteredDoctorList" :key="doc.id" :label="doc.name" :value="doc.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="日期范围">
                        <el-date-picker
                            v-model="dateRange"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="YYYY-MM-DD"
                            style="width: 240px"
                        />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">查询</el-button>
                        <el-button icon="Refresh" @click="handleReset">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="date-group-list">
                <el-card v-for="group in groupedScheduleList" :key="group.workDate" class="date-group-card" shadow="never">
                    <template #header>
                        <div class="group-header">
                            <div class="group-header-main">
                                <div class="group-date">
                                    <el-icon><Calendar /></el-icon>
                                    <span>{{ group.workDate }}</span>
                                </div>
                                <div class="group-meta">
                                    <el-tag size="small" effect="plain" type="primary">{{ group.items.length }} 条排班</el-tag>
                                    <el-tag size="small" effect="plain" type="success">{{ group.availableCount }} 个有号源</el-tag>
                                    <el-tag size="small" effect="plain" type="info">{{ group.totalQuota }} 总号源</el-tag>
                                </div>
                            </div>
                            <div class="group-preview">
                                <span class="preview-label">有号源医生</span>
                                <div class="preview-tags" v-if="group.availableDoctors.length">
                                    <el-tag
                                        v-for="doc in group.availableDoctors.slice(0, 3)"
                                        :key="doc.id"
                                        size="small"
                                        effect="light"
                                        type="success"
                                    >
                                        {{ doc.doctorName }} · {{ doc.remainingQuota }}
                                    </el-tag>
                                    <span v-if="group.availableDoctors.length > 3" class="more-tip">+{{ group.availableDoctors.length - 3 }} 更多</span>
                                </div>
                                <span v-else class="more-tip">当天暂无可预约号源</span>
                            </div>
                        </div>
                    </template>

                    <el-table
                        :data="group.items"
                        v-loading="loading"
                        stripe
                        style="width: 100%"
                        :header-cell-style="{ background: '#f8fafc', color: '#64748b' }"
                    >
                        <el-table-column prop="id" label="ID" width="80" align="center" fixed="left" />

                        <el-table-column label="出诊时段" width="120">
                            <template #default="{ row }">
                                <el-tag :type="row.shiftType === 1 ? 'warning' : 'primary'" effect="light" class="shift-tag">
                                    <el-icon class="mr-1"><component :is="row.shiftType === 1 ? 'Sunny' : 'Moon'" /></el-icon>
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
                                    <el-progress
                                        :percentage="getQuotaPercentage(row.remainingQuota, row.quota)"
                                        :show-text="false"
                                        :stroke-width="4"
                                        :color="getQuotaColorHex(row.remainingQuota)"
                                    />
                                </div>
                            </template>
                        </el-table-column>

                        <el-table-column label="状态" width="100">
                            <template #default="{ row }">
                                <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="plain" round class="status-tag">
                                    <span :class="['dot', row.status === 1 ? 'bg-success' : 'bg-danger']"></span>
                                    {{ row.status === 1 ? '正常' : '停诊' }}
                                </el-tag>
                            </template>
                        </el-table-column>

                        <el-table-column label="操作" fixed="right" width="200" align="center">
                            <template #default="{ row }">
                                <el-button v-if="row.status === 1" type="warning" link size="small" icon="VideoPause" @click="handleStop(row)">
                                    停诊
                                </el-button>
                                <el-button v-else type="success" link size="small" icon="VideoPlay" @click="handleResume(row)">
                                    恢复
                                </el-button>
                                <el-divider direction="vertical" />
                                <el-button type="danger" link size="small" icon="Delete" @click="handleDelete(row)">
                                    删除
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </div>

            <el-card class="table-card" shadow="never" v-if="!groupedScheduleList.length">
                <el-empty description="暂无排班数据" />
            </el-card>

            <div class="pagination-wrapper">
                <el-pagination
                    v-model:current-page="queryForm.pageNum"
                    v-model:page-size="queryForm.pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    background
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </div>

        <el-dialog v-model="dialogVisible" title="发布新排班" width="550px" destroy-on-close align-center>
            <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" class="custom-dialog-form">
                <el-alert title="发布排班前，请确保该医生当日无其他冲突安排" type="info" show-icon :closable="false" class="mb-4" />

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="选择科室" prop="deptId">
                            <el-select v-model="formData.deptId" placeholder="请选择科室" @change="onFormDeptChange" style="width: 100%">
                                <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="选择医生" prop="doctorId">
                            <el-select v-model="formData.doctorId" placeholder="请先选择科室" :disabled="!formData.deptId" style="width: 100%">
                                <el-option v-for="doc in formDoctorList" :key="doc.id" :label="doc.name" :value="doc.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="出诊日期" prop="workDate">
                    <el-date-picker v-model="formData.workDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>

                <el-form-item label="出诊时段" prop="shiftType">
                    <el-radio-group v-model="formData.shiftType">
                        <el-radio-button :value="1">
                            <el-icon class="mr-1"><Sunny /></el-icon> 上午
                        </el-radio-button>
                        <el-radio-button :value="2">
                            <el-icon class="mr-1"><Moon /></el-icon> 下午
                        </el-radio-button>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="号源数量" prop="quota">
                    <el-input-number v-model="formData.quota" :min="1" :max="200" controls-position="right" style="width: 100%" />
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
import {
    Search, Refresh, Plus, Calendar, User, OfficeBuilding,
    Sunny, Moon, UserFilled, VideoPause, VideoPlay, Delete, Back, List
} from '@element-plus/icons-vue';

interface ScheduleGroup {
    workDate: string;
    items: DoctorScheduleVO[];
    availableCount: number;
    totalQuota: number;
    availableDoctors: DoctorScheduleVO[];
}

const router = useRouter();

const scheduleData = ref<DoctorScheduleVO[]>([]);
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

const filteredDoctorList = computed(() => {
    if (!queryForm.deptId) return doctorList.value;
    return doctorList.value.filter(d => d.deptId === queryForm.deptId);
});

const groupedScheduleList = computed<ScheduleGroup[]>(() => {
    const groups = new Map<string, DoctorScheduleVO[]>();

    scheduleData.value.forEach((schedule) => {
        const list = groups.get(schedule.workDate) || [];
        list.push(schedule);
        groups.set(schedule.workDate, list);
    });

    return Array.from(groups.entries()).map(([workDate, items]) => {
        const availableDoctors = items.filter(item => item.remainingQuota > 0);
        return {
            workDate,
            items,
            availableCount: availableDoctors.length,
            totalQuota: items.reduce((sum, item) => sum + (item.quota || 0), 0),
            availableDoctors
        };
    });
});

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

const getQuotaPercentage = (remaining: number, quota: number) => {
    if (!quota) return 0;
    return Math.round((remaining / quota) * 100) || 0;
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
        const res = await scheduleApi.getList({
            ...queryForm,
            pageNum: queryForm.pageNum,
            pageSize: queryForm.pageSize,
            sortField: 'workDate,shiftType',
            sortDirection: 'asc,asc'
        });
        if (res.code === 200) {
            scheduleData.value = res.data.records
                .map(schedule => {
                    const doctor = doctorList.value.find(d => d.id === schedule.doctorId);
                    const dept = departmentList.value.find(d => d.id === schedule.deptId);
                    return {
                        ...schedule,
                        doctorName: doctor?.name || `医生${schedule.doctorId}`,
                        deptName: dept?.deptName || `科室${schedule.deptId}`
                    };
                })
                .sort((a, b) => {
                    const dateCompare = a.workDate.localeCompare(b.workDate);
                    if (dateCompare !== 0) return dateCompare;
                    const shiftCompare = a.shiftType - b.shiftType;
                    if (shiftCompare !== 0) return shiftCompare;
                    return (a.doctorName || '').localeCompare(b.doctorName || '');
                });
            total.value = res.data.total;

            const maxPage = Math.max(1, Math.ceil(res.data.total / (queryForm.pageSize || 10)));
            if ((queryForm.pageNum || 1) > maxPage) {
                queryForm.pageNum = maxPage;
            }
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
    queryForm.pageNum = 1;
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
    min-height: 100vh;
    padding: 20px;
    background:
        radial-gradient(circle at top left, rgba(59, 130, 246, 0.08), transparent 26%),
        #f8fafc;
    font-family: 'Inter', sans-serif;
}

.page-header {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(14px);
    padding: 18px 24px;
    margin: -20px -20px 20px -20px;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
        .page-title {
            margin: 0 0 8px;
            font-size: 20px;
            font-weight: 600;
            color: #0f172a;
        }
    }
}

.main-content {
    max-width: 100%;
}

.search-card {
    margin-bottom: 20px;
    border: 1px solid #e2e8f0;
    border-radius: 24px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.04);

    .search-form {
        :deep(.el-form-item) {
            margin-bottom: 0;
            margin-right: 18px;
        }
    }
}

.date-group-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.date-group-card {
    border: 1px solid #e2e8f0;
    border-radius: 24px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.04);
    overflow: hidden;

    .group-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
        flex-wrap: wrap;
    }

    .group-header-main {
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    .group-date {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #0f172a;
    }

    .group-meta {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
    }

    .group-preview {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        gap: 8px;
        text-align: right;
    }

    .preview-label {
        font-size: 13px;
        color: #64748b;
    }

    .preview-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
        justify-content: flex-end;
    }

    .more-tip {
        color: #94a3b8;
        font-size: 13px;
    }
}

.table-card {
    border: none;
    border-radius: 24px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.04);
}

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
            color: #0f172a;
            font-size: 14px;
        }

        .dept {
            font-size: 12px;
            color: #64748b;
        }
    }
}

.shift-tag {
    display: inline-flex;
    align-items: center;

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
    display: inline-flex;
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
    margin-top: 20px;
}

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
        color: #94a3b8;
        margin-top: 4px;
    }
}

@media (max-width: 768px) {
    .page-container {
        padding: 16px;
    }

    .page-header {
        padding: 16px;
        margin: -16px -16px 16px -16px;
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }

    .group-preview {
        align-items: flex-start !important;
        text-align: left !important;
    }
}
</style>