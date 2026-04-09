<template>
    <div class="doctor-patients-container">
        <div class="portal-header">
            <div class="header-content">
                <div class="title-section">
                    <div class="icon-box"><el-icon>
                            <FirstAidKit />
                        </el-icon></div>
                    <div>
                        <h1 class="page-title">患者接诊管理</h1>
                        <p class="page-subtitle">Outpatient Reception & History</p>
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
            <el-tabs v-model="activeTab" class="custom-tabs" type="border-card">
                <el-tab-pane name="today">
                    <template #label>
                        <span class="custom-tab-label">
                            <el-icon>
                                <Timer />
                            </el-icon> 今日待诊
                            <el-badge :value="pendingCount" class="tab-badge" type="danger" v-if="pendingCount > 0" />
                        </span>
                    </template>

                    <div class="tab-toolbar">
                        <div class="left-info">
                            <el-icon>
                                <Calendar />
                            </el-icon>
                            <span>{{ currentDate }} 出诊列表</span>
                        </div>
                        <el-button icon="Refresh" circle @click="fetchToday" title="刷新列表" />
                    </div>

                    <el-table :data="todayPatients" v-loading="loading" stripe style="width: 100%"
                        :header-cell-style="{ background: '#f9fafb', color: '#6b7280' }">
                        <el-table-column label="患者信息" min-width="180">
                            <template #default="{ row }">
                                <div class="patient-cell">
                                    <el-avatar :size="40" class="p-avatar" icon="UserFilled">
                                        {{ row.patientName ? row.patientName.substring(0, 1) : '' }}
                                    </el-avatar>
                                    <div class="p-info">
                                        <div class="p-name">{{ row.patientName }}</div>
                                        <div class="p-meta">
                                            <el-tag size="small" :type="row.patientGender === 1 ? '' : 'danger'"
                                                effect="plain" class="gender-tag">
                                                <el-icon>
                                                    <component :is="row.patientGender === 1 ? 'Male' : 'Female'" />
                                                </el-icon>
                                            </el-tag>
                                            <span class="age">{{ row.patientAge }}岁</span>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>

                        <el-table-column prop="patientPhone" label="联系方式" width="140">
                            <template #default="{ row }">
                                <div class="icon-text">
                                    <el-icon>
                                        <Phone />
                                    </el-icon> {{ row.patientPhone }}
                                </div>
                            </template>
                        </el-table-column>

                        <el-table-column label="预约时段" width="120">
                            <template #default="{ row }">
                                <el-tag :type="row.shiftType === 1 ? 'warning' : 'info'" effect="light">
                                    {{ row.shiftType === 1 ? '上午' : '下午' }}
                                </el-tag>
                            </template>
                        </el-table-column>

                        <el-table-column label="就诊状态" width="120">
                            <template #default="{ row }">
                                <el-tag :type="row.hasDiagnosis ? 'success' : 'primary'" effect="plain" round>
                                    {{ row.hasDiagnosis ? '已完成' : '候诊中' }}
                                </el-tag>
                            </template>
                        </el-table-column>

                        <el-table-column label="操作" width="160" align="right">
                            <template #default="{ row }">
                                <el-button v-if="!row.hasDiagnosis" type="primary" size="small" round icon="EditPen"
                                    @click="startDiagnosis(row)">
                                    开始接诊
                                </el-button>
                                <el-button v-else type="info" plain size="small" round icon="Document"
                                    @click="viewDiagnosis(row)">
                                    查看记录
                                </el-button>
                            </template>
                        </el-table-column>

                        <template #empty>
                            <el-empty description="今日暂无挂号患者，请稍候..." :image-size="120" />
                        </template>
                    </el-table>
                </el-tab-pane>

                <el-tab-pane name="history">
                    <template #label>
                        <span class="custom-tab-label">
                            <el-icon>
                                <FolderChecked />
                            </el-icon> 历史患者
                        </span>
                    </template>

                    <div class="history-search-bar">
                        <el-input v-model="historyQuery.patientName" placeholder="输入患者姓名搜索..." prefix-icon="Search"
                            clearable style="width: 300px" @keyup.enter="fetchHistory" />
                        <el-button type="primary" @click="fetchHistory">查询</el-button>
                    </div>

                    <el-table :data="historyPatients" v-loading="historyLoading" stripe
                        :header-cell-style="{ background: '#f9fafb', color: '#6b7280' }">
                        <el-table-column prop="patientName" label="患者姓名" width="120">
                            <template #default="{ row }">
                                <span style="font-weight: 600; color: #374151;">{{ row.patientName }}</span>
                            </template>
                        </el-table-column>

                        <el-table-column label="就诊日期" width="180" prop="workDate">
                            <template #default="{ row }">
                                <div class="icon-text">
                                    <el-icon>
                                        <Calendar />
                                    </el-icon>
                                    <span>{{ row.workDate }} ({{ row.shiftType === 1 ? '上午' : '下午' }})</span>
                                </div>
                            </template>
                        </el-table-column>

                        <el-table-column prop="diagnosisResult" label="诊断结果摘要" min-width="200" show-overflow-tooltip />

                        <el-table-column label="操作" width="120" align="center">
                            <template #default="{ row }">
                                <el-button type="primary" link icon="View" @click="viewHistoryDiagnosis(row)">
                                    详情
                                </el-button>
                            </template>
                        </el-table-column>

                        <template #empty>
                            <el-empty description="暂无历史就诊记录" :image-size="100" />
                        </template>
                    </el-table>

                    <div class="pagination-wrapper" v-if="historyPatients.length > 0">
                        <el-pagination layout="prev, pager, next" :total="100"
                            v-model:current-page="historyQuery.pageNum" @current-change="fetchHistory" />
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>

        <el-dialog v-model="diagnosisDialogVisible" title="患者接诊处理" width="650px" destroy-on-close align-center
            class="diagnosis-dialog">
            <div class="patient-banner" v-if="currentPatient">
                <div class="banner-item">
                    <span class="label">当前患者：</span>
                    <span class="val highlight">{{ currentPatient.patientName }}</span>
                </div>
                <div class="banner-item">
                    <span class="label">性别：</span>
                    <span class="val">{{ currentPatient.patientGender === 1 ? '男' : '女' }}</span>
                </div>
                <div class="banner-item">
                    <span class="label">年龄：</span>
                    <span class="val">{{ currentPatient.patientAge }}岁</span>
                </div>
                <div class="banner-item">
                    <span class="label">挂号类型：</span>
                    <span class="val">普通门诊</span>
                </div>
            </div>

            <el-form ref="diagnosisFormRef" :model="diagnosisForm" label-position="top" class="diagnosis-form">
                <el-form-item label="主诉 / 症状" required>
                    <el-input v-model="diagnosisForm.symptom" type="textarea" :rows="3"
                        placeholder="描述患者主诉、现病史及体征..." />
                </el-form-item>

                <el-form-item label="初步诊断" required>
                    <el-input v-model="diagnosisForm.diagnosisResult" type="textarea" :rows="2"
                        placeholder="请输入医学诊断结论..." />
                </el-form-item>

                <el-form-item label="处方 / 医嘱">
                    <el-input v-model="diagnosisForm.prescription" type="textarea" :rows="4"
                        placeholder="开具药品、检查项目或诊疗建议..." />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="diagnosisDialogVisible = false">取消接诊</el-button>
                <el-button type="primary" @click="submitDiagnosis" :loading="submitting">
                    提交并归档
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { doctorPortalApi } from '@/api/doctorPortal';
import type { TodayPatient, DiagnosisCreateDTO } from '@/types/doctor-portal';
import dayjs from 'dayjs';
// 引入图标
import {
    FirstAidKit, Back, Timer, FolderChecked, Calendar, Refresh,
    UserFilled, Phone, Male, Female, EditPen, Document, Search, View
} from '@element-plus/icons-vue';

const router = useRouter();

const activeTab = ref('today');
const loading = ref(false);
const todayPatients = ref<TodayPatient[]>([]);
const currentDate = dayjs().format('YYYY-MM-DD');

const historyLoading = ref(false);
const historyPatients = ref<any[]>([]);
const historyQuery = reactive({ patientName: '', pageNum: 1, pageSize: 20 });

const diagnosisDialogVisible = ref(false);
const submitting = ref(false);
const currentPatient = ref<TodayPatient | null>(null);
const diagnosisForm = reactive<DiagnosisCreateDTO>({
    appointmentId: 0,
    symptom: '',
    diagnosisResult: '',
    prescription: ''
});

// 计算今日待诊数量
const pendingCount = computed(() => {
    return todayPatients.value.filter(p => !p.hasDiagnosis).length;
});

const fetchToday = async () => {
    loading.value = true;
    try {
        const res = await doctorPortalApi.getTodayPatients();
        if (res.code === 200) {
            todayPatients.value = res.data;
        }
    } catch (error) {
        console.error('获取失败', error);
    } finally {
        loading.value = false;
    }
};

const fetchHistory = async () => {
    historyLoading.value = true;
    try {
        const res = await doctorPortalApi.getHistoryPatients(historyQuery);
        if (res.code === 200) {
            historyPatients.value = res.data.records;
        }
    } catch (error) {
        console.error('获取失败', error);
    } finally {
        historyLoading.value = false;
    }
};

const startDiagnosis = (patient: TodayPatient) => {
    currentPatient.value = patient;
    Object.assign(diagnosisForm, {
        appointmentId: patient.appointmentId,
        symptom: '',
        diagnosisResult: '',
        prescription: ''
    });
    diagnosisDialogVisible.value = true;
};

const submitDiagnosis = async () => {
    if (!diagnosisForm.symptom || !diagnosisForm.diagnosisResult) {
        ElMessage.warning('请填写症状和诊断结果');
        return;
    }
    submitting.value = true;
    try {
        await doctorPortalApi.createDiagnosis(diagnosisForm);
        ElMessage.success('诊疗记录提交成功');
        diagnosisDialogVisible.value = false;
        fetchToday();
    } catch (error) {
        console.error('提交失败', error);
    } finally {
        submitting.value = false;
    }
};

const viewDiagnosis = (patient: TodayPatient) => {
    router.push(`/doctor/diagnosis?appointmentId=${patient.appointmentId}`);
};

const viewHistoryDiagnosis = (row: any) => {
    router.push(`/doctor/diagnosis?id=${row.diagnosisId}`);
};

onMounted(() => {
    fetchToday();
    fetchHistory();
});
</script>

<style scoped lang="scss">
:deep(:root) {
    --el-color-primary: #7c3aed;
}

.doctor-patients-container {
    min-height: 100vh;
    background-color: #f3f4f6;
    font-family: 'Inter', sans-serif;
}

/* 1. 页头 */
.portal-header {
    background: white;
    height: 80px;
    border-bottom: 1px solid #e5e7eb;
    padding: 0 40px;
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
            color: #7c3aed;
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

/* 2. Tabs 样式重写 */
.custom-tabs {
    background: white;
    border: none;
    border-radius: 12px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    overflow: hidden;

    :deep(.el-tabs__header) {
        background: #f9fafb;
        border-bottom: 1px solid #e5e7eb;
    }

    :deep(.el-tabs__content) {
        padding: 24px;
    }

    .custom-tab-label {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 15px;

        .tab-badge {
            margin-top: -8px;
            margin-left: 2px;
        }
    }
}

/* Tab 1 工具栏 */
.tab-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .left-info {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 15px;
        font-weight: 600;
        color: #374151;
    }
}

/* 表格内患者信息 */
.patient-cell {
    display: flex;
    align-items: center;
    gap: 12px;

    .p-avatar {
        background: #f3f4f6;
        color: #9ca3af;
    }

    .p-info {
        .p-name {
            font-weight: 600;
            color: #1f2937;
            font-size: 15px;
        }

        .p-meta {
            display: flex;
            align-items: center;
            gap: 6px;
            margin-top: 4px;

            .age {
                font-size: 12px;
                color: #6b7280;
            }
        }
    }

    .gender-tag {
        height: 18px;
        padding: 0 4px;
        font-size: 12px;
    }
}

.icon-text {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #4b5563;
}

/* Tab 2 历史搜索 */
.history-search-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px dashed #e5e7eb;
}

/* 弹窗样式 */
.patient-banner {
    background: #f3e8ff;
    padding: 16px 24px;
    border-radius: 8px;
    display: flex;
    gap: 40px;
    margin-bottom: 24px;
    border: 1px solid #e9d5ff;

    .banner-item {
        font-size: 14px;

        .label {
            color: #6b7280;
        }

        .val {
            color: #1f2937;
            font-weight: 600;
        }

        .highlight {
            font-size: 16px;
            color: #7c3aed;
        }
    }
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}
</style>