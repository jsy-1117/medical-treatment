<template>
    <div class="appointments-container">
        <el-header class="app-header">
            <div class="header-inner">
                <div class="brand">
                    <el-icon :size="24" class="brand-icon">
                        <Tickets />
                    </el-icon>
                    <div>
                        <h1>我的挂号</h1>
                        <p>My Appointments</p>
                    </div>
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
                        <div class="header-copy">
                            <span class="title"><el-icon><List /></el-icon> 挂号记录</span>
                            <span class="sub-title">统一查看就诊状态、时间和操作</span>
                        </div>
                        <el-button type="primary" link icon="Refresh" :loading="loading" @click="fetchData">
                            刷新列表
                        </el-button>
                    </div>
                </template>

                <el-table
                    :data="appointmentList"
                    v-loading="loading"
                    style="width: 100%"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }"
                >
                    <el-table-column label="医生信息" min-width="180">
                        <template #default="{ row }">
                            <div class="doctor-info">
                                <el-avatar :size="44" icon="UserFilled" class="avatar" />
                                <div class="info-text">
                                    <div class="name">{{ row.doctorName }}</div>
                                    <el-tag size="small" type="info" effect="plain">{{ row.deptName }}</el-tag>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="就诊时间" width="200">
                        <template #default="{ row }">
                            <div class="time-info">
                                <div class="date"><el-icon><Calendar /></el-icon> {{ row.workDate }}</div>
                                <el-tag size="small" :type="row.shiftType === 1 ? 'warning' : 'info'" effect="light">
                                    {{ row.shiftType === 1 ? '上午 (08:00-12:00)' : '下午 (14:00-18:00)' }}
                                </el-tag>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="当前状态" width="120">
                        <template #default="{ row }">
                            <el-tag :type="getStatusType(row.status)" effect="light" round class="status-tag">
                                <el-icon v-if="row.status === 1"><CircleCheckFilled /></el-icon>
                                <el-icon v-else-if="row.status === 2"><CircleCloseFilled /></el-icon>
                                <el-icon v-else><Clock /></el-icon>
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
                            <el-button v-if="row.status === 0" type="danger" plain size="small" round icon="Close" @click="handleCancel(row)">
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
import {
    Tickets, HomeFilled, List, Refresh, UserFilled,
    Calendar, CircleCheckFilled, CircleCloseFilled, Clock, Close
} from '@element-plus/icons-vue';

const patientStore = usePatientStore();
const appointmentList = ref<AppointmentVO[]>([]);
const loading = ref(false);

const getStatusType = (status: number) => {
    switch (status) {
        case 0: return 'primary';
        case 1: return 'success';
        case 2: return 'info';
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
        '确定要取消该次预约吗？取消后不可恢复。',
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
    background:
        radial-gradient(circle at top left, rgba(59, 130, 246, 0.08), transparent 26%),
        #f8fafc;
    font-family: 'Inter', sans-serif;
}

.app-header {
    position: sticky;
    top: 0;
    z-index: 100;
    height: auto;
    padding: 0;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(14px);
    border-bottom: 1px solid #e2e8f0;

    .header-inner {
        max-width: 1200px;
        margin: 0 auto;
        min-height: 70px;
        padding: 0 24px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
    }

    .brand {
        display: flex;
        align-items: center;
        gap: 10px;

        .brand-icon {
            color: #3b82f6;
            background: #eff6ff;
            padding: 6px;
            border-radius: 8px;
            box-sizing: content-box;
        }

        h1 {
            margin: 0;
            font-size: 18px;
            color: #0f172a;
        }

        p {
            margin: 2px 0 0;
            color: #94a3b8;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.08em;
        }
    }
}

.main-content {
    max-width: 1200px;
    margin: 24px auto 0;
    padding: 0 20px 56px;
}

.table-card {
    border-radius: 24px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.04);
    overflow: hidden;

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 16px;

        .header-copy {
            display: flex;
            flex-direction: column;
            gap: 4px;
        }

        .title {
            font-size: 16px;
            font-weight: 600;
            color: #0f172a;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .sub-title {
            color: #64748b;
            font-size: 13px;
        }
    }
}

.doctor-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .avatar {
        background: #f8fafc;
        color: #cbd5e1;
        border: 2px solid #fff;
        box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
    }

    .info-text {
        .name {
            font-weight: 600;
            color: #0f172a;
            margin-bottom: 4px;
        }
    }
}

.time-info {
    .date {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #334155;
        font-weight: 500;
        margin-bottom: 6px;
    }
}

.status-tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
}

.meta-text {
    color: #94a3b8;
    font-size: 13px;
}

.text-placeholder {
    color: #cbd5e1;
}

@media (max-width: 768px) {
    .app-header .header-inner {
        padding: 14px 16px;
        flex-direction: column;
        align-items: flex-start;
    }

    .main-content {
        padding: 0 16px 40px;
    }

    .table-card .card-header {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>