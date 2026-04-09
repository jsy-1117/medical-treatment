<template>
    <div class="appointments-container">
        <el-header class="app-header">
            <div class="header-inner">
                <div class="brand">
                    <el-icon :size="24" class="brand-icon">
                        <Tickets />
                    </el-icon>
                    <h1>我的挂号 <span class="subtitle">My Appointments</span></h1>
                </div>
                <el-button type="primary" plain round icon="HomeFilled" @click="$router.push('/')">
                    返回首页
                </el-button>
            </div>
        </el-header>

        <div class="main-content">
            <el-card class="table-card" shadow="never">
                <template #header>
                    <div class="card-header">
                        <span class="title">
                            <el-icon>
                                <List />
                            </el-icon> 挂号记录列表
                        </span>
                        <el-button type="primary" link icon="Refresh" :loading="loading" @click="fetchData">
                            刷新列表
                        </el-button>
                    </div>
                </template>

                <el-table :data="appointmentList" v-loading="loading" style="width: 100%"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
                    <el-table-column label="医生信息" min-width="180">
                        <template #default="{ row }">
                            <div class="doctor-info">
                                <el-avatar :size="44" icon="UserFilled" class="avatar" />
                                <div class="info-text">
                                    <div class="name">{{ row.doctorName }}</div>
                                    <div class="dept">
                                        <el-tag size="small" type="info" effect="plain">{{ row.deptName }}</el-tag>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="就诊时间" width="200">
                        <template #default="{ row }">
                            <div class="time-info">
                                <div class="date"><el-icon>
                                        <Calendar />
                                    </el-icon> {{ row.workDate }}</div>
                                <div class="shift">
                                    <el-tag size="small" :type="row.shiftType === 1 ? 'warning' : 'info'"
                                        effect="light">
                                        {{ row.shiftType === 1 ? '上午 (08:00-12:00)' : '下午 (14:00-18:00)' }}
                                    </el-tag>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="当前状态" width="120">
                        <template #default="{ row }">
                            <el-tag :type="getStatusType(row.status)" effect="light" round class="status-tag">
                                <el-icon v-if="row.status === 1">
                                    <CircleCheckFilled />
                                </el-icon>
                                <el-icon v-else-if="row.status === 2">
                                    <CircleCloseFilled />
                                </el-icon>
                                <el-icon v-else>
                                    <Clock />
                                </el-icon>
                                {{ getStatusText(row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="提交时间" width="180">
                        <template #default="{ row }">
                            <span class="meta-text">{{ row.createTime }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" width="120" fixed="right" align="right">
                        <template #default="{ row }">
                            <el-button v-if="row.status === 0" type="danger" plain size="small" round icon="Close"
                                @click="handleCancel(row)">
                                取消挂号
                            </el-button>
                            <span v-else class="text-placeholder">-</span>
                        </template>
                    </el-table-column>

                    <template #empty>
                        <el-empty description="暂无挂号记录" :image-size="120">
                            <el-button type="primary" @click="$router.push('/book')">去挂号</el-button>
                        </el-empty>
                    </template>
                </el-table>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePatientStore } from '@/stores/patient';
import { appointmentApi } from '@/api/appointment';
import type { AppointmentVO } from '@/types/appointment';
// 引入图标
import {
    Tickets, HomeFilled, List, Refresh, UserFilled,
    Calendar, CircleCheckFilled, CircleCloseFilled, Clock, Close
} from '@element-plus/icons-vue';

const patientStore = usePatientStore();
const appointmentList = ref<AppointmentVO[]>([]);
const loading = ref(false);

const getStatusType = (status: number) => {
    switch (status) {
        case 0: return 'primary'; // 待就诊用蓝色，表示进行中
        case 1: return 'success'; // 完成用绿色
        case 2: return 'info';    // 取消用灰色，降低视觉干扰
        default: return 'info';
    }
};

const getStatusText = (status: number) => {
    switch (status) {
        case 0: return '待就诊';
        case 1: return '已完成';
        case 2: return '已取消';
        default: return '未知';
    }
};

const fetchData = async () => {
    if (!patientStore.patientInfo?.id) return;
    loading.value = true;
    try {
        // 这里假设 API 支持分页，传入较大的 pageSize 以获取全部记录，或者你需要实现分页组件
        const res = await appointmentApi.getPatientAppointments(patientStore.patientInfo.id, 1, 100);
        if (res.code === 200) {
            appointmentList.value = res.data.records;
        }
    } catch (error) {
        console.error('获取挂号记录失败', error);
    } finally {
        loading.value = false;
    }
};

const handleCancel = (row: AppointmentVO) => {
    ElMessageBox.confirm(
        '确定要取消该次预约吗？取消后不可恢复，需重新挂号。',
        '取消提示',
        {
            type: 'warning',
            confirmButtonText: '确定取消',
            cancelButtonText: '再想想'
        }
    ).then(async () => {
        try {
            await appointmentApi.cancel(row.id, patientStore.patientInfo!.id);
            ElMessage.success('预约已取消');
            fetchData();
        } catch (error) {
            console.error('取消失败', error);
        }
    });
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped lang="scss">
:deep(:root) {
    --el-color-primary: #3b82f6;
}

.appointments-container {
    min-height: 100vh;
    background-color: #f8fafc;
    font-family: 'Inter', sans-serif;
}

/* 顶部导航 */
.app-header {
    background: #ffffff;
    border-bottom: 1px solid #e2e8f0;
    height: 64px;
    padding: 0 40px;

    .header-inner {
        max-width: 1200px;
        margin: 0 auto;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .brand {
        display: flex;
        align-items: center;
        gap: 10px;

        .brand-icon {
            color: #3b82f6;
            background: #eff6ff;
            padding: 6px;
            border-radius: 6px;
        }

        h1 {
            font-size: 20px;
            color: #1e293b;
            margin: 0;
            display: flex;
            align-items: baseline;
            gap: 8px;

            .subtitle {
                font-size: 13px;
                color: #94a3b8;
                font-weight: 400;
            }
        }
    }
}

.main-content {
    max-width: 1200px;
    margin: 30px auto;
    padding: 0 20px;
}

/* 卡片与表格样式 */
.table-card {
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title {
            font-size: 16px;
            font-weight: 600;
            color: #334155;
            display: flex;
            align-items: center;
            gap: 8px;
        }
    }
}

/* 表格内部样式 */
.doctor-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .avatar {
        background: #f1f5f9;
        color: #94a3b8;
        border: 1px solid #e2e8f0;
    }

    .info-text {
        .name {
            font-weight: 600;
            color: #1e293b;
            font-size: 14px;
            margin-bottom: 2px;
        }
    }
}

.time-info {
    .date {
        color: #334155;
        font-weight: 500;
        margin-bottom: 4px;
        display: flex;
        align-items: center;
        gap: 6px;
    }
}

.status-tag {
    display: flex;
    align-items: center;
    gap: 4px;
    width: fit-content;
}

.meta-text {
    color: #94a3b8;
    font-size: 13px;
}

.text-placeholder {
    color: #cbd5e1;
}
</style>