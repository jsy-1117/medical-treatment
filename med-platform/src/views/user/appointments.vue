<template>
    <PatientLayout>
        <div class="appointments-page">
            <div class="page-card">
                <div class="page-card-header">
                    <div>
                        <div class="card-title"><el-icon><List /></el-icon> 挂号记录</div>
                        <div class="card-subtitle">统一查看就诊状态、时间和操作</div>
                    </div>
                    <el-button plain icon="Refresh" :loading="loading" @click="fetchData">刷新列表</el-button>
                </div>

                <div class="tl-list" v-loading="loading">
                    <div v-for="row in appointmentList" :key="row.id" class="tl-row">
                        <div class="tl-left">
                            <div class="tl-date-block">
                                <span class="tl-date-num">{{ formatDay(row.workDate) }}</span>
                                <span class="tl-date-month">{{ formatMonth(row.workDate) }}</span>
                            </div>
                            <div class="tl-line"></div>
                        </div>
                        <div class="tl-right">
                            <div class="tl-card" :class="'tl-status-' + row.status">
                                <div class="tl-card-top">
                                    <div class="tl-doctor-info">
                                        <el-avatar :size="40" icon="UserFilled" class="tl-avatar" />
                                        <div>
                                            <div class="tl-doctor-name">{{ row.doctorName }}</div>
                                            <div class="tl-doctor-dept">
                                                <span class="tl-tag tag-primary">{{ row.deptName }}</span>
                                                <span class="tl-tag" :class="row.shiftType === 1 ? 'tag-am' : 'tag-pm'">
                                                    {{ row.shiftType === 1 ? '上午' : '下午' }}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    <el-tag :type="statusType(row.status)" effect="light" round class="tl-status-tag">
                                        {{ statusText(row.status) }}
                                    </el-tag>
                                </div>
                                <div class="tl-card-meta">
                                    <span><el-icon><Clock /></el-icon> 提交时间：{{ row.createTime }}</span>
                                </div>
                                <div class="tl-card-action" v-if="row.status === 0">
                                    <el-button type="danger" plain size="small" round icon="Close" @click="handleCancel(row)">
                                        取消挂号
                                    </el-button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <el-empty v-if="!loading && appointmentList.length === 0" description="暂无挂号记录" :image-size="120">
                        <el-button type="primary" @click="$router.push('/book')">去挂号</el-button>
                    </el-empty>
                </div>
            </div>
        </div>
    </PatientLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePatientStore } from '@/stores/patient';
import { appointmentApi } from '@/api/appointment';
import type { AppointmentVO } from '@/types/appointment';
import PatientLayout from '@/components/PatientLayout.vue';
import { List, Refresh, UserFilled, Clock, Close } from '@element-plus/icons-vue';

const patientStore = usePatientStore();
const appointmentList = ref<AppointmentVO[]>([]);
const loading = ref(false);

const statusType = (status: number) => {
    switch (status) {
        case 0: return 'primary';
        case 1: return 'success';
        case 2: return 'info';
        default: return 'info';
    }
};

const statusText = (status: number) => {
    switch (status) {
        case 0: return '待就诊';
        case 1: return '已完成';
        case 2: return '已取消';
        default: return '未知';
    }
};

const formatDay = (dateStr: string | undefined) => {
    if (!dateStr) return '';
    return dateStr.split('-')[2] || '';
};

const formatMonth = (dateStr: string | undefined) => {
    if (!dateStr) return '';
    const p = dateStr.split('-');
    return p.length === 3 ? `${p[1]}/${p[0]!.slice(2)}` : '';
};

const fetchData = async () => {
    if (!patientStore.patientInfo?.id) return;
    loading.value = true;
    try {
        const res = await appointmentApi.getPatientAppointments(patientStore.patientInfo!.id, 1, 100);
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
.appointments-page {
    animation: fadeUp 0.4s ease both;
}

.page-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(26, 138, 122, 0.06);
}

.page-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid #eef0ec;
}

.card-title {
    font-size: 16px;
    font-weight: 600;
    color: #1a2a2a;
    display: inline-flex;
    align-items: center;
    gap: 8px;
}

.card-subtitle {
    font-size: 13px;
    color: #9aabab;
    margin-top: 2px;
}

.tl-list {
    padding: 20px 24px;
    min-height: 120px;
}

.tl-row {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;

    &:last-child {
        margin-bottom: 0;
    }
}

.tl-left {
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 56px;
}

.tl-date-block {
    background: #eef0ec;
    border-radius: 10px;
    padding: 8px 12px;
    text-align: center;
    line-height: 1.2;
}

.tl-date-num {
    display: block;
    font-size: 18px;
    font-weight: 700;
    color: #1a2a2a;
}

.tl-date-month {
    display: block;
    font-size: 11px;
    color: #5a6a6a;
}

.tl-line {
    flex: 1;
    width: 2px;
    background: #dee5e2;
    margin: 8px 0;
}

.tl-right {
    flex: 1;
    min-width: 0;
}

.tl-card {
    background: #fafbf9;
    border-radius: 12px;
    padding: 16px 18px;
    border: 1px solid #eef0ec;
    transition: box-shadow 0.2s;

    &:hover {
        box-shadow: 0 4px 14px rgba(26, 138, 122, 0.06);
    }
}

.tl-card-top {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 12px;
    margin-bottom: 10px;
}

.tl-doctor-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.tl-avatar {
    background: #f0f2ef;
    color: #bcc5c2;
    border: 2px solid white;
    box-shadow: 0 4px 10px rgba(26, 138, 122, 0.06);
    flex-shrink: 0;
}

.tl-doctor-name {
    font-weight: 600;
    color: #1a2a2a;
    font-size: 14px;
    margin-bottom: 4px;
}

.tl-doctor-dept {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
}

.tl-tag {
    display: inline-flex;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 11px;
    font-weight: 500;
}

.tag-primary {
    background: #e6f4f1;
    color: #1a8a7a;
}

.tag-am {
    background: #fef3c7;
    color: #b45309;
}

.tag-pm {
    background: #e0eefc;
    color: #1d6fc7;
}

.tl-status-tag {
    font-size: 12px;
    flex-shrink: 0;
}

.tl-card-meta {
    font-size: 12px;
    color: #9aabab;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-bottom: 8px;
}

.tl-card-action {
    padding-top: 8px;
    border-top: 1px solid #eef0ec;
}

@keyframes fadeUp {
    from {
        opacity: 0;
        transform: translateY(12px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@media (max-width: 640px) {
    .tl-row {
        flex-direction: column;
        gap: 8px;
    }

    .tl-left {
        flex-direction: row;
        gap: 12px;
        min-width: auto;
    }

    .tl-line {
        width: auto;
        height: 2px;
        flex: 1;
        margin: 0;
    }

    .tl-card-top {
        flex-direction: column;
    }

    .tl-list {
        padding: 16px;
    }
}
</style>
