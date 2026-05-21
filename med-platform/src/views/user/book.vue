<template>
    <PatientLayout>
        <div class="book-page">
            <!-- 搜索筛选 -->
            <div class="search-card">
                <div>
                    <span class="search-eyebrow">快速筛选</span>
                    <h2>选择科室查看可预约排班</h2>
                    <p>选择科室后即可查看对应的医生排班与号源情况</p>
                </div>
                <div class="search-row">
                    <el-select v-model="queryForm.deptId" placeholder="全部科室" clearable size="large" style="width: 220px" @change="fetchSchedules">
                        <template #prefix><el-icon><OfficeBuilding /></el-icon></template>
                        <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
                    </el-select>
                    <el-button type="primary" size="large" icon="Search" @click="fetchSchedules">刷新排班</el-button>
                </div>
            </div>

            <!-- 排班列表 -->
            <div class="schedule-section">
                <div class="schedule-header">
                    <div class="schedule-title">
                        <el-icon><Clock /></el-icon>
                        <span>可预约排班</span>
                    </div>
                    <span class="schedule-count">{{ scheduleList.length }} 条结果</span>
                </div>

                <div class="schedule-table">
                    <div v-loading="loading" class="schedule-list">
                        <div v-for="row in scheduleList" :key="row.id" class="schedule-row">
                            <div class="s-cell doc-cell">
                                <el-avatar :size="44" icon="UserFilled" class="s-avatar" />
                                <div>
                                    <div class="s-name">{{ row.doctorName }}</div>
                                    <div class="s-meta">
                                        <span class="s-tag tag-primary">{{ row.doctorJobTitle }}</span>
                                        <span>{{ row.deptName }}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="s-cell">
                                <div class="s-date"><el-icon><Calendar /></el-icon> {{ row.workDate }}</div>
                                <span class="s-tag" :class="row.shiftType === 1 ? 'tag-am' : 'tag-pm'">
                                    {{ row.shiftType === 1 ? '上午 08:00-12:00' : '下午 14:00-18:00' }}
                                </span>
                            </div>
                            <div class="s-cell s-quota">
                                <div class="quota-bar">
                                    <span :class="quotaTextClass(row.remainingQuota)">
                                        {{ row.remainingQuota > 0 ? `剩余 ${row.remainingQuota} 号` : '已约满' }}
                                    </span>
                                    <div class="q-bar">
                                        <div class="q-fill" :style="{ width: quotaPercent(row.remainingQuota), background: quotaColor(row.remainingQuota) }"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="s-cell s-action">
                                <el-button v-if="row.remainingQuota > 0" type="primary" round @click="handleBook(row)">
                                    立即挂号
                                </el-button>
                                <el-button v-else disabled plain round>已满额</el-button>
                            </div>
                        </div>
                        <el-empty v-if="!loading && scheduleList.length === 0" description="当前筛选条件下暂无排班" :image-size="100" />
                    </div>
                </div>
            </div>
        </div>
    </PatientLayout>
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
import PatientLayout from '@/components/PatientLayout.vue';
import {
    Calendar, Search, OfficeBuilding,
    UserFilled, Clock
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

const quotaTextClass = (count: number) => {
    if (count <= 0) return 'q-empty';
    if (count < 5) return 'q-low';
    return 'q-ok';
};

const quotaPercent = (count: number) => {
    if (count <= 0) return '100%';
    return `${Math.min((count / 30) * 100, 100)}%`;
};

const quotaColor = (count: number) => {
    if (count <= 0) return '#e74c3c';
    if (count < 10) return '#f59e0b';
    return '#1a8a7a';
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
.book-page {
    animation: fadeUp 0.4s ease both;
}

.search-card {
    background: white;
    border-radius: 16px;
    padding: 24px 28px;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 24px;
    flex-wrap: wrap;
    box-shadow: 0 2px 10px rgba(26, 138, 122, 0.06);

    .search-eyebrow {
        display: inline-flex;
        padding: 4px 10px;
        border-radius: 12px;
        background: #e6f4f1;
        color: #1a8a7a;
        font-size: 12px;
        font-weight: 600;
        letter-spacing: 0.03em;
        margin-bottom: 10px;
    }

    h2 {
        margin: 0 0 6px;
        font-size: 20px;
        font-weight: 650;
        color: #1a2a2a;
    }

    p {
        margin: 0;
        color: #5a6a6a;
        font-size: 13px;
    }
}

.search-row {
    display: flex;
    gap: 12px;
    align-items: center;
    flex-wrap: wrap;
}

.schedule-section {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(26, 138, 122, 0.06);
}

.schedule-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 18px 24px;
    border-bottom: 1px solid #eef0ec;
}

.schedule-title {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #1a2a2a;
    font-size: 15px;
}

.schedule-count {
    color: #9aabab;
    font-size: 13px;
}

.schedule-list {
    min-height: 100px;
}

.schedule-row {
    display: flex;
    align-items: center;
    padding: 16px 24px;
    border-bottom: 1px solid #f5f6f3;
    transition: background 0.15s;
    gap: 16px;

    &:last-child {
        border-bottom: none;
    }

    &:hover {
        background: #fafbf9;
    }
}

.s-cell {
    flex: 1;
    min-width: 0;
}

.doc-cell {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 2;
}

.s-avatar {
    background: #f0f2ef;
    color: #bcc5c2;
    border: 2px solid #f7f8f5;
    box-shadow: 0 4px 12px rgba(26, 138, 122, 0.06);
    flex-shrink: 0;
}

.s-name {
    font-weight: 600;
    color: #1a2a2a;
    font-size: 14px;
    margin-bottom: 4px;
}

.s-meta {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #5a6a6a;
}

.s-date {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #1a2a2a;
    font-weight: 500;
    font-size: 14px;
    margin-bottom: 4px;
}

.s-quota {
    max-width: 160px;
}

.s-action {
    text-align: right;
    flex-shrink: 0;
    min-width: 120px;
}

.s-tag {
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

.quota-bar {
    display: flex;
    flex-direction: column;
    gap: 4px;

    span {
        font-size: 13px;
        font-weight: 500;
    }

    .q-empty {
        color: #9aabab;
    }

    .q-low {
        color: #e74c3c;
    }

    .q-ok {
        color: #1a8a7a;
    }
}

.q-bar {
    height: 4px;
    background: #eef0ec;
    border-radius: 2px;
    overflow: hidden;
}

.q-fill {
    height: 100%;
    border-radius: 2px;
    transition: width 0.4s;
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

@media (max-width: 768px) {
    .schedule-row {
        flex-wrap: wrap;
        padding: 16px;
    }

    .doc-cell {
        flex: 1 1 100%;
    }

    .s-quota {
        max-width: none;
        flex: 1;
    }

    .s-action {
        flex-shrink: 1;
        min-width: auto;
    }

    .search-card {
        padding: 20px;
    }
}
</style>
