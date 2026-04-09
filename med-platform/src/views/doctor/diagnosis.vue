<template>
    <div class="diagnosis-container">
        <div class="portal-header">
            <div class="header-content">
                <div class="title-section">
                    <div class="icon-box"><el-icon>
                            <DocumentChecked />
                        </el-icon></div>
                    <div>
                        <h1 class="page-title">诊疗记录档案</h1>
                        <p class="page-subtitle">Medical Record & Diagnosis History</p>
                    </div>
                </div>
                <div class="action-section">
                    <el-button type="primary" plain round icon="Back" @click="goBack">
                        {{ diagnosisDetail ? '返回列表' : '返回工作台' }}
                    </el-button>
                </div>
            </div>
        </div>

        <div class="main-content">
            <transition name="el-fade-in" mode="out-in">
                <div v-if="diagnosisDetail" class="detail-wrapper" key="detail">
                    <el-card class="medical-record-card" shadow="never">
                        <template #header>
                            <div class="record-header">
                                <div class="header-left">
                                    <span class="record-id">NO.{{ String(diagnosisDetail.id).padStart(6, '0') }}</span>
                                    <el-tag type="success" effect="plain" round size="small">已归档</el-tag>
                                </div>
                                <div class="header-right">
                                    <span class="record-date">
                                        <el-icon>
                                            <Calendar />
                                        </el-icon> {{ diagnosisDetail.createTime }}
                                    </span>
                                    <el-divider direction="vertical" />
                                    <el-button type="primary" link icon="EditPen" @click="openEditDialog">
                                        修订记录
                                    </el-button>
                                </div>
                            </div>
                        </template>

                        <div class="record-body">
                            <div class="patient-banner">
                                <el-avatar :size="50" class="avatar" icon="UserFilled">
                                    {{ diagnosisDetail.patientName?.substring(0, 1) }}
                                </el-avatar>
                                <div class="info-group">
                                    <div class="name">{{ diagnosisDetail.patientName }}</div>
                                    <div class="meta">
                                        <el-tag size="small" type="info">门诊</el-tag>
                                        <span class="ml-2">就诊日期: {{ diagnosisDetail.workDate }} ({{
                                            diagnosisDetail.shiftType === 1 ? '上午' :
                                            '下午' }})</span>
                                    </div>
                                </div>
                            </div>

                            <div class="clinical-section">
                                <div class="section-item">
                                    <div class="sec-title"><el-icon>
                                            <ChatLineSquare />
                                        </el-icon> 主诉 / 症状</div>
                                    <div class="sec-content highlight">{{ diagnosisDetail.symptom || '未记录' }}</div>
                                </div>

                                <el-divider border-style="dashed" />

                                <div class="section-item">
                                    <div class="sec-title"><el-icon>
                                            <Aim />
                                        </el-icon> 诊断结果</div>
                                    <div class="sec-content">{{ diagnosisDetail.diagnosisResult || '未记录' }}</div>
                                </div>

                                <el-divider border-style="dashed" />

                                <div class="section-item">
                                    <div class="sec-title"><el-icon>
                                            <FirstAidKit />
                                        </el-icon> 处方 / 医嘱</div>
                                    <div class="sec-content prescription-font">{{ diagnosisDetail.prescription ||
                                        '未开具处方' }}</div>
                                </div>
                            </div>
                        </div>

                        <div class="record-footer">
                            <div class="signature">
                                <span>主治医师：</span>
                                <span class="doc-sign">{{ doctorStore.doctorInfo?.name }}</span>
                            </div>
                        </div>
                    </el-card>
                </div>

                <div v-else class="list-wrapper" key="list">
                    <el-card class="list-card" shadow="never">
                        <template #header>
                            <div class="card-header">
                                <span class="title">历史接诊记录</span>
                                <div class="subtitle">共 {{ total }} 份档案</div>
                            </div>
                        </template>

                        <el-table :data="diagnosisList" v-loading="loading" stripe
                            :header-cell-style="{ background: '#f9fafb', color: '#6b7280' }">
                            <el-table-column label="患者信息" width="140">
                                <template #default="{ row }">
                                    <div class="patient-cell">
                                        <el-avatar :size="32" class="p-avatar" icon="User">
                                            {{ row.patientName?.substring(0, 1) }}
                                        </el-avatar>
                                        <span class="p-name">{{ row.patientName }}</span>
                                    </div>
                                </template>
                            </el-table-column>

                            <el-table-column label="就诊信息" width="200">
                                <template #default="{ row }">
                                    <div class="visit-info">
                                        <div class="date"><el-icon>
                                                <Calendar />
                                            </el-icon> {{ row.workDate }}</div>
                                        <div class="shift">
                                            <el-tag size="small" :type="row.shiftType === 1 ? 'warning' : 'info'"
                                                effect="light">
                                                {{ row.shiftType === 1 ? '上午' : '下午' }}
                                            </el-tag>
                                        </div>
                                    </div>
                                </template>
                            </el-table-column>

                            <el-table-column label="诊断摘要" min-width="250" show-overflow-tooltip>
                                <template #default="{ row }">
                                    <span class="diag-summary">{{ row.diagnosisResult || '暂无诊断信息' }}</span>
                                </template>
                            </el-table-column>

                            <el-table-column label="建档时间" width="180" prop="createTime" />

                            <el-table-column label="操作" width="100" align="center">
                                <template #default="{ row }">
                                    <el-button type="primary" link size="small" icon="View" @click="viewDetail(row)">
                                        调阅
                                    </el-button>
                                </template>
                            </el-table-column>

                            <template #empty>
                                <el-empty description="暂无历史诊疗记录" :image-size="100" />
                            </template>
                        </el-table>

                        <div class="pagination-wrapper">
                            <el-pagination v-model:current-page="queryForm.pageNum"
                                v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next"
                                background @current-change="fetchList" />
                        </div>
                    </el-card>
                </div>
            </transition>
        </div>

        <el-dialog v-model="editDialogVisible" title="修订病历信息" width="600px" destroy-on-close align-center>
            <el-form :model="editForm" label-position="top">
                <el-form-item label="主诉 / 症状">
                    <el-input v-model="editForm.symptom" type="textarea" :rows="3" placeholder="患者症状描述..." />
                </el-form-item>
                <el-form-item label="诊断结果">
                    <el-input v-model="editForm.diagnosisResult" type="textarea" :rows="3" placeholder="医学诊断结论..." />
                </el-form-item>
                <el-form-item label="处方 / 医嘱">
                    <el-input v-model="editForm.prescription" type="textarea" :rows="4" placeholder="开具处方药或医嘱..." />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleUpdate" :loading="updating">保存修订</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useDoctorStore } from '@/stores/doctor';
import { doctorPortalApi } from '@/api/doctorPortal';
import type { DiagnosisVO, DiagnosisUpdateDTO } from '@/types/doctor-portal';
// 引入图标
import {
    DocumentChecked, Back, Calendar, EditPen, UserFilled, User,
    ChatLineSquare, Aim, FirstAidKit, View
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const doctorStore = useDoctorStore();

const loading = ref(false);
const diagnosisList = ref<DiagnosisVO[]>([]);
const diagnosisDetail = ref<DiagnosisVO | null>(null);
const total = ref(0);

const queryForm = reactive({ pageNum: 1, pageSize: 10 });

const editDialogVisible = ref(false);
const updating = ref(false);
const editForm = reactive<DiagnosisUpdateDTO>({
    symptom: '',
    diagnosisResult: '',
    prescription: ''
});

// 获取列表
const fetchList = async () => {
    loading.value = true;
    try {
        const res = await doctorPortalApi.getDiagnosisList(queryForm);
        if (res.code === 200) {
            diagnosisList.value = res.data.records;
            total.value = res.data.total;
        }
    } catch (error) {
        console.error('获取失败', error);
    } finally {
        loading.value = false;
    }
};

// 获取详情
const fetchDetail = async (id?: number, appointmentId?: number) => {
    loading.value = true;
    try {
        let res;
        if (id) {
            res = await doctorPortalApi.getDiagnosisById(id);
        } else if (appointmentId) {
            res = await doctorPortalApi.getDiagnosisByAppointmentId(appointmentId);
        }
        if (res && res.code === 200 && res.data) {
            diagnosisDetail.value = res.data;
        }
    } catch (error) {
        console.error('获取详情失败', error);
    } finally {
        loading.value = false;
    }
};

// 页面导航逻辑
const goBack = () => {
    if (diagnosisDetail.value) {
        // 如果当前是详情模式
        if (route.query.id || route.query.appointmentId) {
            // 如果是通过 URL 进来的，返回上一页或者清空 query
            router.push('/doctor/diagnosis'); // 跳转回纯列表页
            diagnosisDetail.value = null; // 切换视图
        } else {
            diagnosisDetail.value = null; // 仅切换视图
        }
    } else {
        // 如果当前是列表模式，返回首页
        router.push('/doctor/home');
    }
};

const viewDetail = (row: DiagnosisVO) => {
    // 仅更新视图数据，不一定非要跳转路由，但为了支持刷新保留状态，建议跳转
    // 这里采用简单方式：直接由 router 控制
    router.push(`/doctor/diagnosis?id=${row.id}`);
    fetchDetail(row.id);
};

const openEditDialog = () => {
    if (diagnosisDetail.value) {
        editForm.symptom = diagnosisDetail.value.symptom || '';
        editForm.diagnosisResult = diagnosisDetail.value.diagnosisResult || '';
        editForm.prescription = diagnosisDetail.value.prescription || '';
        editDialogVisible.value = true;
    }
};

const handleUpdate = async () => {
    if (!diagnosisDetail.value) return;
    updating.value = true;
    try {
        await doctorPortalApi.updateDiagnosis(diagnosisDetail.value.id, editForm);
        ElMessage.success('病历更新成功');
        editDialogVisible.value = false;
        fetchDetail(diagnosisDetail.value.id);
    } catch (error) {
        console.error('更新失败', error);
    } finally {
        updating.value = false;
    }
};

onMounted(() => {
    const id = route.query.id ? Number(route.query.id) : undefined;
    const appointmentId = route.query.appointmentId ? Number(route.query.appointmentId) : undefined;

    if (id || appointmentId) {
        fetchDetail(id, appointmentId);
    } else {
        fetchList();
    }
});
</script>

<style scoped lang="scss">
:deep(:root) {
    --el-color-primary: #7c3aed;
    /* 医生端紫色系 */
}

.diagnosis-container {
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
            font-family: 'Roboto', sans-serif;
        }
    }
}

.main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 40px;
}

/* 2. 详情页 - 电子病历卡片 */
.medical-record-card {
    border: none;
    border-radius: 4px;
    /* 病历卡片稍微方正一点 */
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    background: #fff;
    min-height: 600px;
    position: relative;

    /* 顶部装饰条 */
    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, #7c3aed, #c4b5fd);
    }

    .record-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-bottom: 10px;

        .header-left {
            display: flex;
            align-items: center;
            gap: 12px;

            .record-id {
                font-family: 'Roboto Mono', monospace;
                color: #9ca3af;
                font-size: 14px;
            }
        }

        .header-right {
            display: flex;
            align-items: center;

            .record-date {
                color: #6b7280;
                font-size: 14px;
                display: flex;
                align-items: center;
                gap: 6px;
            }
        }
    }

    .patient-banner {
        background: #f9fafb;
        padding: 20px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 32px;
        border: 1px solid #e5e7eb;

        .avatar {
            background: #e0e7ff;
            color: #4338ca;
        }

        .info-group {
            .name {
                font-size: 18px;
                font-weight: 600;
                color: #1f2937;
                margin-bottom: 4px;
            }

            .meta {
                font-size: 13px;
                color: #6b7280;
            }
        }

        .ml-2 {
            margin-left: 12px;
        }
    }

    .clinical-section {
        padding: 0 10px;

        .section-item {
            margin: 20px 0;

            .sec-title {
                font-size: 15px;
                font-weight: 600;
                color: #7c3aed;
                margin-bottom: 12px;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .sec-content {
                font-size: 15px;
                line-height: 1.6;
                color: #374151;
                padding-left: 24px;
                white-space: pre-wrap;

                &.highlight {
                    font-weight: 500;
                    color: #111827;
                }

                &.prescription-font {
                    font-family: 'Georgia', serif;
                    color: #4b5563;
                }
            }
        }
    }

    .record-footer {
        margin-top: 60px;
        padding-top: 20px;
        border-top: 2px solid #f3f4f6;
        text-align: right;

        .signature {
            display: inline-block;
            text-align: center;

            span {
                display: block;
                color: #9ca3af;
                font-size: 12px;
                margin-bottom: 8px;
            }

            .doc-sign {
                font-family: 'Dancing Script', cursive, serif;
                /* 模拟签名 */
                font-size: 24px;
                color: #1f2937;
                border-bottom: 1px solid #d1d5db;
                padding: 0 20px 4px;
            }
        }
    }
}

/* 3. 列表页 */
.list-card {
    border: none;
    border-radius: 12px;

    .card-header {
        display: flex;
        align-items: baseline;
        gap: 10px;

        .title {
            font-size: 16px;
            font-weight: 600;
            color: #374151;
        }

        .subtitle {
            font-size: 12px;
            color: #9ca3af;
        }
    }
}

.patient-cell {
    display: flex;
    align-items: center;
    gap: 10px;

    .p-avatar {
        background: #f3f4f6;
        color: #9ca3af;
    }

    .p-name {
        font-weight: 500;
        color: #1f2937;
    }
}

.visit-info {
    .date {
        color: #374151;
        font-weight: 500;
        margin-bottom: 4px;
        display: flex;
        align-items: center;
        gap: 6px;
    }
}

.diag-summary {
    color: #4b5563;
    font-size: 13px;
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
}
</style>