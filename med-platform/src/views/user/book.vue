<template>
    <div class="book-container">
        <el-header class="book-header">
            <div class="header-inner">
                <div class="brand">
                    <el-icon :size="24" class="brand-icon">
                        <Calendar />
                    </el-icon>
                    <h1>预约挂号 <span class="subtitle">Appointment</span></h1>
                </div>
                <el-button type="primary" plain round icon="HomeFilled" @click="$router.push('/')">
                    返回首页
                </el-button>
            </div>
        </el-header>

        <div class="main-content">
            <div class="search-section">
                <div class="search-card">
                    <div class="search-title">
                        <el-icon>
                            <Search />
                        </el-icon> 查找专家与排班
                    </div>
                    <el-form inline class="custom-form">
                        <el-form-item label="选择科室">
                            <el-select v-model="queryForm.deptId" placeholder="全部科室" clearable size="large"
                                style="width: 220px" @change="fetchSchedules">
                                <template #prefix><el-icon>
                                        <OfficeBuilding />
                                    </el-icon></template>
                                <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName"
                                    :value="dept.id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" size="large" icon="Search" @click="fetchSchedules">
                                刷新排班
                            </el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </div>

            <el-card class="schedule-card" shadow="never">
                <el-table :data="scheduleList" v-loading="loading"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }" style="width: 100%">
                    <el-table-column label="专家信息" min-width="180">
                        <template #default="{ row }">
                            <div class="doctor-info-cell">
                                <el-avatar :size="48" icon="UserFilled" class="doctor-avatar" src="" />
                                <div class="doctor-text">
                                    <div class="name">
                                        {{ row.doctorName }}
                                        <el-tag size="small" effect="plain" type="primary" round>{{ row.doctorJobTitle
                                            }}</el-tag>
                                    </div>
                                    <div class="dept">{{ row.deptName }}</div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="就诊日期" width="160">
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
                            <el-tag :type="row.shiftType === 1 ? 'warning' : 'info'" effect="light" class="shift-tag">
                                <el-icon class="mr-1">
                                    <component :is="row.shiftType === 1 ? 'Sunny' : 'Moon'" />
                                </el-icon>
                                {{ row.shiftType === 1 ? '上午' : '下午' }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column label="号源状态" width="140">
                        <template #default="{ row }">
                            <div class="quota-cell">
                                <span :class="getQuotaClass(row.remainingQuota)">
                                    {{ row.remainingQuota > 0 ? `剩余 ${row.remainingQuota}` : '已约满' }}
                                </span>
                                <el-progress :percentage="getQuotaPercentage(row.remainingQuota)"
                                    :status="getQuotaStatus(row.remainingQuota)" :show-text="false" :stroke-width="4"
                                    style="width: 80px; margin-top: 4px;" />
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" width="120" align="right">
                        <template #default="{ row }">
                            <el-button v-if="row.remainingQuota > 0" type="primary" size="default" round
                                @click="handleBook(row)">
                                立即挂号
                            </el-button>
                            <el-button v-else type="info" disabled plain round>
                                缺货登记
                            </el-button>
                        </template>
                    </el-table-column>

                    <template #empty>
                        <el-empty description="当前筛选条件下暂无排班" :image-size="100" />
                    </template>
                </el-table>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePatientStore } from '@/stores/patient';
import { publicApi } from '@/api/public';
import { appointmentApi } from '@/api/appointment';
import type { DepartmentVO } from '@/types/department';
import type { SchedulePublicVO } from '@/types/public';
// 引入图标
import {
    Calendar, HomeFilled, Search, OfficeBuilding,
    UserFilled, Sunny, Moon
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const patientStore = usePatientStore();

const departmentList = ref<DepartmentVO[]>([]);
const scheduleList = ref<SchedulePublicVO[]>([]);
const loading = ref(false);

const queryForm = reactive({
    deptId: undefined as number | undefined,
    doctorId: undefined as number | undefined
});

// 获取科室列表
const fetchDepartments = async () => {
    try {
        const res = await publicApi.getDepartments();
        if (res.code === 200) {
            departmentList.value = res.data;
        }
    } catch (error) {
        console.error('获取科室列表失败', error);
    }
};

// 获取排班列表
const fetchSchedules = async () => {
    loading.value = true;
    try {
        const res = await publicApi.getAvailableSchedules({
            deptId: queryForm.deptId,
            doctorId: queryForm.doctorId,
            pageNum: 1,
            pageSize: 50
        });
        if (res.code === 200) {
            scheduleList.value = res.data.records;
        }
    } catch (error) {
        console.error('获取排班列表失败', error);
    } finally {
        loading.value = false;
    }
};

// UI 辅助函数：号源文字样式
const getQuotaClass = (count: number) => {
    if (count <= 0) return 'text-gray';
    if (count < 5) return 'text-danger';
    return 'text-success';
};

// UI 辅助函数：号源进度条百分比（模拟，假设总量20）
const getQuotaPercentage = (count: number) => {
    if (count <= 0) return 100;
    // 假设每个时段大概20-30个号，反向计算被占用的比例用于展示
    // 这里为了视觉效果，简单处理：剩余越多，进度条越短(表示被占用少) 或者 进度条表示剩余量
    // 让我们用"剩余量"概念：满格是充裕
    return Math.min((count / 30) * 100, 100);
};

// UI 辅助函数：进度条颜色状态
const getQuotaStatus = (count: number) => {
    if (count <= 0) return 'exception'; // 红
    if (count < 10) return 'warning';   // 橙
    return 'success';                   // 绿
};

// 挂号
const handleBook = (schedule: SchedulePublicVO) => {
    if (!patientStore.isProfileComplete()) {
        ElMessageBox.confirm(
            '您的个人信息不完整，请先完善信息后再进行挂号。',
            '提示',
            {
                confirmButtonText: '去完善',
                cancelButtonText: '取消',
                type: 'warning'
            }
        ).then(() => {
            router.push('/user/home');
        });
        return;
    }

    ElMessageBox.confirm(
        `确定挂号 ${schedule.doctorName} 医生 (${schedule.workDate} ${schedule.shiftType === 1 ? '上午' : '下午'}) 吗？`,
        '确认挂号',
        { type: 'info', confirmButtonText: '确认支付/挂号' }
    ).then(async () => {
        try {
            await appointmentApi.create({
                patientId: patientStore.patientInfo!.id,
                doctorId: schedule.doctorId,
                scheduleId: schedule.id
            });
            ElMessage.success('挂号成功');
            fetchSchedules();
        } catch (error) {
            console.error('挂号失败', error);
        }
    });
};

onMounted(() => {
    if (route.query.deptId) {
        queryForm.deptId = Number(route.query.deptId);
    }
    if (route.query.doctorId) {
        queryForm.doctorId = Number(route.query.doctorId);
    }
    fetchDepartments();
    fetchSchedules();
});
</script>

<style scoped lang="scss">
:deep(:root) {
    --el-color-primary: #3b82f6;
}

.book-container {
    min-height: 100vh;
    background-color: #f8fafc;
    font-family: 'Inter', sans-serif;
}

/* 顶部 Header */
.book-header {
    background: #ffffff;
    border-bottom: 1px solid #e2e8f0;
    height: 64px;
    padding: 0 40px;
    position: sticky;
    top: 0;
    z-index: 100;

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
            box-sizing: content-box;
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

/* 搜索筛选区 */
.search-section {
    margin-bottom: 24px;

    .search-card {
        background: white;
        padding: 24px 32px;
        border-radius: 16px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);
        border: 1px solid #e2e8f0;
        display: flex;
        align-items: center;
        gap: 40px;
    }

    .search-title {
        font-size: 16px;
        font-weight: 600;
        color: #1e293b;
        display: flex;
        align-items: center;
        gap: 8px;
        white-space: nowrap;
    }

    .custom-form {
        flex: 1;

        :deep(.el-form-item) {
            margin-bottom: 0;
            margin-right: 20px;
        }
    }
}

/* 排班列表卡片 */
.schedule-card {
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);
    overflow: hidden;

    :deep(.el-table__row) {
        height: 80px;
    }

    /* 医生信息样式 */
    .doctor-info-cell {
        display: flex;
        align-items: center;
        gap: 12px;

        .doctor-avatar {
            background: #f1f5f9;
            color: #cbd5e1;
            border: 2px solid white;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }

        .doctor-text {
            .name {
                font-weight: 600;
                color: #1e293b;
                font-size: 15px;
                margin-bottom: 4px;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .dept {
                color: #64748b;
                font-size: 13px;
            }
        }
    }

    /* 日期样式 */
    .date-cell {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #334155;
        font-weight: 500;
    }

    /* 号源样式 */
    .quota-cell {
        display: flex;
        flex-direction: column;
        justify-content: center;

        span {
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 2px;
        }

        .text-success {
            color: #10b981;
        }

        .text-danger {
            color: #f59e0b;
        }

        .text-gray {
            color: #94a3b8;
        }
    }

    .shift-tag {
        display: flex;
        align-items: center;
        width: fit-content;
    }

    .mr-1 {
        margin-right: 4px;
    }
}
</style>