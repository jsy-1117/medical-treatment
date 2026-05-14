<template>
    <div class="book-container">
        <el-header class="book-header">
            <div class="header-inner">
                <div class="brand">
                    <el-icon :size="24" class="brand-icon">
                        <Calendar />
                    </el-icon>
                    <div>
                        <h1>预约挂号</h1>
                        <p>Appointment</p>
                    </div>
                </div>
                <el-button type="primary" plain round icon="HomeFilled" @click="$router.push('/')">
                    返回首页
                </el-button>
            </div>
        </el-header>

        <div class="main-content">
            <section class="search-card">
                <div class="search-copy">
                    <span class="eyebrow">快速筛选</span>
                    <h2>选择科室并查看可预约排班</h2>
                    <p>点击右侧选择条件</p>
                </div>

                <el-form inline class="custom-form">
                    <el-form-item label="选择科室">
                        <el-select v-model="queryForm.deptId" placeholder="全部科室" clearable size="large" style="width: 240px" @change="fetchSchedules">
                            <template #prefix><el-icon><OfficeBuilding /></el-icon></template>
                            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" size="large" icon="Search" @click="fetchSchedules">刷新排班</el-button>
                    </el-form-item>
                </el-form>
            </section>

            <el-card class="schedule-card" shadow="never">
                <template #header>
                    <div class="card-header">
                        <span class="title-with-icon"><el-icon><Clock /></el-icon> 可预约排班</span>
                        <span class="header-tip">{{ scheduleList.length }} 条结果</span>
                    </div>
                </template>

                <el-table
                    :data="scheduleList"
                    v-loading="loading"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }"
                    style="width: 100%"
                >
                    <el-table-column label="专家信息" min-width="200">
                        <template #default="{ row }">
                            <div class="doctor-info-cell">
                                <el-avatar :size="44" icon="UserFilled" class="doctor-avatar" />
                                <div class="doctor-text">
                                    <div class="name">{{ row.doctorName }}</div>
                                    <div class="dept">
                                        <el-tag size="small" effect="plain" type="primary" round>{{ row.doctorJobTitle }}</el-tag>
                                        <span>{{ row.deptName }}</span>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="就诊日期" width="160">
                        <template #default="{ row }">
                            <div class="date-cell">
                                <el-icon><Calendar /></el-icon>
                                <span>{{ row.workDate }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="时段" width="120">
                        <template #default="{ row }">
                            <el-tag :type="row.shiftType === 1 ? 'warning' : 'info'" effect="light" class="shift-tag">
                                <el-icon class="mr-1"><component :is="row.shiftType === 1 ? 'Sunny' : 'Moon'" /></el-icon>
                                {{ row.shiftType === 1 ? '上午' : '下午' }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column label="号源状态" width="150">
                        <template #default="{ row }">
                            <div class="quota-cell">
                                <span :class="getQuotaClass(row.remainingQuota)">{{ row.remainingQuota > 0 ? `剩余 ${row.remainingQuota}` : '已约满' }}</span>
                                <el-progress
                                    :percentage="getQuotaPercentage(row.remainingQuota)"
                                    :status="getQuotaStatus(row.remainingQuota)"
                                    :show-text="false"
                                    :stroke-width="4"
                                />
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" width="120" align="right">
                        <template #default="{ row }">
                            <el-button v-if="row.remainingQuota > 0" type="primary" size="default" round @click="handleBook(row)">
                                立即挂号
                            </el-button>
                            <el-button v-else type="info" disabled plain round>
                                已满额
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
import {
    Calendar, HomeFilled, Search, OfficeBuilding,
    UserFilled, Sunny, Moon, Clock
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

const getQuotaClass = (count: number) => {
    if (count <= 0) return 'text-gray';
    if (count < 5) return 'text-danger';
    return 'text-success';
};

const getQuotaPercentage = (count: number) => {
    if (count <= 0) return 100;
    return Math.min((count / 30) * 100, 100);
};

const getQuotaStatus = (count: number) => {
    if (count <= 0) return 'exception';
    if (count < 10) return 'warning';
    return 'success';
};

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
        { type: 'info', confirmButtonText: '确认挂号' }
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
    background:
        radial-gradient(circle at top left, rgba(59, 130, 246, 0.08), transparent 26%),
        #f8fafc;
    font-family: 'Inter', sans-serif;
}

.book-header {
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

.search-card {
    background: rgba(255, 255, 255, 0.96);
    border: 1px solid #e2e8f0;
    border-radius: 24px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.04);
    padding: 24px;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    gap: 24px;
    align-items: center;
    flex-wrap: wrap;

    .search-copy {
        max-width: 420px;

        .eyebrow {
            display: inline-flex;
            padding: 6px 12px;
            border-radius: 999px;
            background: #eff6ff;
            color: #2563eb;
            font-size: 12px;
            font-weight: 600;
            letter-spacing: 0.04em;
            text-transform: uppercase;
            margin-bottom: 12px;
        }

        h2 {
            margin: 0 0 8px;
            font-size: 24px;
            color: #0f172a;
        }

        p {
            margin: 0;
            color: #64748b;
            line-height: 1.7;
        }
    }

    .custom-form {
        margin-left: auto;

        :deep(.el-form-item) {
            margin-bottom: 0;
            margin-right: 16px;
        }
    }
}

.schedule-card {
    border-radius: 24px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.04);
    overflow: hidden;

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title-with-icon {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            color: #0f172a;
        }

        .header-tip {
            color: #94a3b8;
            font-size: 13px;
        }
    }

    :deep(.el-table__row) {
        height: 76px;
    }

    .doctor-info-cell {
        display: flex;
        align-items: center;
        gap: 12px;

        .doctor-avatar {
            background: #f8fafc;
            color: #cbd5e1;
            border: 2px solid #fff;
            box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
        }

        .doctor-text {
            .name {
                font-weight: 600;
                color: #0f172a;
                font-size: 14px;
                margin-bottom: 4px;
            }

            .dept {
                display: flex;
                align-items: center;
                gap: 8px;
                color: #64748b;
                font-size: 13px;
            }
        }
    }

    .date-cell {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #334155;
        font-weight: 500;
    }

    .quota-cell {
        display: flex;
        flex-direction: column;
        gap: 6px;

        span {
            font-size: 13px;
            font-weight: 600;
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
        display: inline-flex;
        align-items: center;
    }

    .mr-1 {
        margin-right: 4px;
    }
}

@media (max-width: 768px) {
    .book-header .header-inner {
        padding: 14px 16px;
        flex-direction: column;
        align-items: flex-start;
    }

    .main-content {
        padding: 0 16px 40px;
    }

    .search-card {
        padding: 20px;
    }
}
</style>