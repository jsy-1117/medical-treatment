<template>
    <div class="doctor-portal-container">
        <div class="portal-header">
            <div class="header-content">
                <div class="title-section">
                    <div class="icon-box"><el-icon>
                            <Calendar />
                        </el-icon></div>
                    <div>
                        <h1 class="page-title">排班工作台</h1>
                        <p class="page-subtitle">管理您的出诊时间与号源状态</p>
                    </div>
                </div>
                <div class="action-section">
                    <el-button type="primary" plain round icon="Back" @click="$router.push('/doctor/home')">
                        返回工作台
                    </el-button>
                </div>
            </div>
        </div>

        <div class="main-content">
            <el-card class="schedule-card" shadow="never">
                <template #header>
                    <div class="card-header">
                        <span class="title">排班列表</span>
                        <el-form :model="queryForm" inline class="header-form">
                            <el-form-item label="日期范围">
                                <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
                                    start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
                                    :shortcuts="dateShortcuts" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" icon="Search" @click="fetchData">查询</el-button>
                                <el-button icon="Refresh" @click="handleReset">重置</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </template>

                <el-table :data="scheduleList" v-loading="loading" stripe
                    :header-cell-style="{ background: '#f9fafb', color: '#6b7280' }">
                    <el-table-column prop="workDate" label="出诊日期" width="160">
                        <template #default="{ row }">
                            <div class="date-cell">
                                <el-icon>
                                    <Calendar />
                                </el-icon>
                                <span>{{ row.workDate }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="时段" width="120">
                        <template #default="{ row }">
                            <el-tag :type="row.shiftType === 1 ? 'warning' : 'primary'" effect="light"
                                class="shift-tag">
                                <el-icon class="mr-1">
                                    <component :is="row.shiftType === 1 ? 'Sunny' : 'Moon'" />
                                </el-icon>
                                {{ row.shiftTypeDesc || (row.shiftType === 1 ? '上午' : '下午') }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column label="号源情况 (余/总)" min-width="200">
                        <template #default="{ row }">
                            <div class="quota-progress">
                                <div class="quota-text">
                                    <span class="num highlight">{{ row.remainingQuota }}</span>
                                    <span class="divider">/</span>
                                    <span class="num total">{{ row.quota }}</span>
                                </div>
                                <el-progress :percentage="calculatePercentage(row.remainingQuota, row.quota)"
                                    :status="getQuotaStatus(row.remainingQuota)" :stroke-width="6" :show-text="false" />
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="排班状态" width="120">
                        <template #default="{ row }">
                            <div class="status-indicator">
                                <span :class="['dot', row.status === 1 ? 'active' : 'inactive']"></span>
                                {{ row.status === 1 ? '正常出诊' : '已停诊' }}
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" width="150" align="right">
                        <template #default="{ row }">
                            <el-button v-if="row.status === 1" type="danger" size="small" plain round
                                icon="SwitchButton" @click="handleStop(row)">
                                停诊
                            </el-button>
                            <el-button v-else type="success" size="small" plain round icon="VideoPlay"
                                @click="handleResume(row)">
                                恢复
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="pagination-wrapper">
                    <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
                        :total="total" layout="total, sizes, prev, pager, next" :page-sizes="[10, 20, 50]" background
                        @size-change="fetchData" @current-change="fetchData" />
                </div>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { doctorPortalApi } from '@/api/doctorPortal';
import type { DoctorScheduleVO } from '@/types/doctor-portal';
import {
    Calendar, Search, Refresh, Back, Sunny, Moon,
    SwitchButton, VideoPlay
} from '@element-plus/icons-vue';

const loading = ref(false);
const scheduleList = ref<DoctorScheduleVO[]>([]);
const total = ref(0);
const dateRange = ref<[string, string] | null>(null);

const queryForm = reactive({
    pageNum: 1,
    pageSize: 10,
    startDate: undefined as string | undefined,
    endDate: undefined as string | undefined
});

const dateShortcuts = [
    {
        text: '未来一周', value: () => {
            const end = new Date();
            const start = new Date();
            end.setTime(start.getTime() + 3600 * 1000 * 24 * 7);
            return [start, end];
        }
    },
    {
        text: '未来一月', value: () => {
            const end = new Date();
            const start = new Date();
            end.setTime(start.getTime() + 3600 * 1000 * 24 * 30);
            return [start, end];
        }
    },
];

// 计算号源百分比（用于进度条：剩余/总数）
const calculatePercentage = (remain: number, total: number) => {
    if (total === 0) return 0;
    return Math.round((remain / total) * 100);
};

const getQuotaStatus = (remain: number) => {
    if (remain === 0) return 'exception';
    if (remain < 5) return 'warning';
    return 'success';
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
        const res = await doctorPortalApi.getScheduleList(queryForm);
        if (res.code === 200) {
            scheduleList.value = res.data.records;
            total.value = res.data.total;
        }
    } catch (error) {
        console.error('获取排班失败', error);
    } finally {
        loading.value = false;
    }
};

const handleReset = () => {
    dateRange.value = null;
    queryForm.pageNum = 1;
    fetchData();
};

const handleStop = (row: DoctorScheduleVO) => {
    ElMessageBox.confirm('确定停诊该时段吗？停诊后患者将无法挂号。', '停诊警告', {
        confirmButtonText: '确定停诊',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await doctorPortalApi.updateScheduleStatus(row.id, 0);
            ElMessage.success('已暂停该时段挂号');
            fetchData();
        } catch (error) {
            console.error('操作失败', error);
        }
    });
};

const handleResume = (row: DoctorScheduleVO) => {
    ElMessageBox.confirm('确定恢复该排班吗？', '恢复排班', {
        confirmButtonText: '确定恢复',
        cancelButtonText: '取消',
        type: 'success'
    }).then(async () => {
        try {
            await doctorPortalApi.updateScheduleStatus(row.id, 1);
            ElMessage.success('排班已恢复');
            fetchData();
        } catch (error) {
            console.error('操作失败', error);
        }
    });
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped lang="scss">
.doctor-portal-container {
    min-height: 100vh;
    background-color: #f3f4f6;
    font-family: 'Inter', sans-serif;
}

/* 医生端页头 - 紫色系 */
.portal-header {
    background: white;
    padding: 0 40px;
    height: 80px;
    border-bottom: 1px solid #e5e7eb;
    margin-bottom: 24px;

    .header-content {
        max-width: 1200px;
        margin: 0 auto;
        height: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .title-section {
        display: flex;
        align-items: center;
        gap: 16px;

        .icon-box {
            width: 48px;
            height: 48px;
            background: #f3e8ff;
            /* 浅紫背景 */
            color: #7c3aed;
            /* 医生端主色：紫色 */
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
        }

        .page-title {
            margin: 0;
            font-size: 20px;
            font-weight: 700;
            color: #1f2937;
        }

        .page-subtitle {
            margin: 4px 0 0;
            font-size: 13px;
            color: #6b7280;
        }
    }
}

.main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 40px;
}

/* 卡片样式 */
.schedule-card {
    border-radius: 16px;
    border: none;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title {
            font-size: 16px;
            font-weight: 600;
            color: #374151;
            padding-left: 10px;
            border-left: 3px solid #7c3aed;
        }

        .header-form {
            :deep(.el-form-item) {
                margin-bottom: 0;
            }
        }
    }
}

/* 表格内容美化 */
.date-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
    color: #374151;
}

.shift-tag {
    display: flex;
    align-items: center;
    width: fit-content;

    .mr-1 {
        margin-right: 4px;
    }
}

/* 号源进度条 */
.quota-progress {
    max-width: 180px;

    .quota-text {
        display: flex;
        align-items: baseline;
        gap: 2px;
        margin-bottom: 6px;
        font-size: 13px;

        .num {
            font-weight: 600;
        }

        .highlight {
            color: #10b981;
            font-size: 15px;
        }

        .divider {
            color: #d1d5db;
            margin: 0 2px;
        }

        .total {
            color: #9ca3af;
        }
    }
}

/* 状态指示点 */
.status-indicator {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;

    .dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;

        &.active {
            background-color: #10b981;
            box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
        }

        &.inactive {
            background-color: #ef4444;
            box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.2);
        }
    }
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
}
</style>