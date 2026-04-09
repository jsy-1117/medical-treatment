<template>
    <div class="doctor-home-container">
        <header class="portal-header">
            <div class="header-content">
                <div class="brand">
                    <div class="logo-box">
                        <el-icon>
                            <Monitor />
                        </el-icon>
                    </div>
                    <div class="titles">
                        <h1>医生工作站</h1>
                        <span class="subtitle">Doctor Workstation</span>
                    </div>
                </div>
                <div class="user-actions">
                    <div class="welcome-text">
                        <span>{{ timePeriod }}好，</span>
                        <span class="doctor-name">{{ doctorStore.doctorInfo?.name }}</span>
                    </div>
                    <el-divider direction="vertical" />
                    <el-button type="danger" plain round size="small" icon="SwitchButton" @click="handleLogout">
                        退出
                    </el-button>
                </div>
            </div>
        </header>

        <div class="main-content">
            <el-row :gutter="24">
                <el-col :span="7">
                    <el-card class="profile-card" shadow="hover" :body-style="{ padding: 0 }">
                        <div class="card-bg"></div>
                        <div class="card-content">
                            <el-avatar :size="72" class="avatar" src="" icon="UserFilled" />
                            <h2 class="name">{{ doctorStore.doctorInfo?.name }}</h2>
                            <div class="tags">
                                <el-tag effect="dark" type="primary" round class="job-tag">
                                    {{ doctorStore.doctorInfo?.jobTitle }}
                                </el-tag>
                            </div>

                            <div class="info-grid">
                                <div class="info-item">
                                    <label>联系电话</label>
                                    <span>{{ doctorStore.doctorInfo?.phone || '未设置' }}</span>
                                </div>
                                <div class="info-item">
                                    <label>所属科室</label>
                                    <span>临床科室</span>
                                </div>
                            </div>

                            <el-button class="pwd-btn" link type="primary" icon="Lock"
                                @click="showPasswordDialog = true">
                                修改登录密码
                            </el-button>
                        </div>
                    </el-card>

                    <div class="section-label">快捷导航</div>
                    <div class="quick-grid">
                        <div class="quick-tile primary" @click="$router.push('/doctor/patients')">
                            <div class="icon-wrapper"><el-icon>
                                    <List />
                                </el-icon></div>
                            <div class="text">
                                <h3>今日接诊</h3>
                                <p>查看待诊患者</p>
                            </div>
                        </div>
                        <div class="quick-tile success" @click="$router.push('/doctor/schedule')">
                            <div class="icon-wrapper"><el-icon>
                                    <Calendar />
                                </el-icon></div>
                            <div class="text">
                                <h3>我的排班</h3>
                                <p>管理出诊时间</p>
                            </div>
                        </div>
                        <div class="quick-tile warning" @click="$router.push('/doctor/diagnosis')">
                            <div class="icon-wrapper"><el-icon>
                                    <Document />
                                </el-icon></div>
                            <div class="text">
                                <h3>诊疗记录</h3>
                                <p>历史病例查询</p>
                            </div>
                        </div>
                    </div>
                </el-col>

                <el-col :span="17">
                    <el-card class="list-card" shadow="never">
                        <template #header>
                            <div class="card-header">
                                <div class="header-left">
                                    <div class="icon-box"><el-icon>
                                            <BellFilled />
                                        </el-icon></div>
                                    <span class="title">今日待诊</span>
                                    <el-tag type="danger" effect="plain" round size="small" v-if="pendingCount > 0">
                                        {{ pendingCount }} 人候诊
                                    </el-tag>
                                </div>
                                <el-button type="primary" link icon="Refresh" :loading="loading"
                                    @click="fetchTodayPatients">
                                    刷新列表
                                </el-button>
                            </div>
                        </template>

                        <el-table :data="todayPatients" v-loading="loading" stripe style="width: 100%"
                            :header-cell-style="{ background: '#f9fafb', color: '#6b7280' }">
                            <el-table-column label="患者信息" min-width="140">
                                <template #default="{ row }">
                                    <div class="patient-cell">
                                        <el-avatar :size="36" class="p-avatar" icon="User">
                                            {{ row.patientName ? row.patientName.substring(0, 1) : '' }}
                                        </el-avatar>
                                        <div class="p-info">
                                            <div class="p-name">{{ row.patientName }}</div>
                                            <div class="p-age">
                                                <el-icon v-if="row.patientGender === 1" color="#409eff">
                                                    <Male />
                                                </el-icon>
                                                <el-icon v-else color="#f56c6c">
                                                    <Female />
                                                </el-icon>
                                                {{ row.patientAge }}岁
                                            </div>
                                        </div>
                                    </div>
                                </template>
                            </el-table-column>

                            <el-table-column label="挂号时段" width="120">
                                <template #default="{ row }">
                                    <el-tag :type="row.shiftType === 1 ? 'warning' : 'info'" effect="light"
                                        class="shift-tag">
                                        <el-icon class="mr-1">
                                            <component :is="row.shiftType === 1 ? 'Sunny' : 'Moon'" />
                                        </el-icon>
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

                            <el-table-column label="操作" width="140" align="right">
                                <template #default="{ row }">
                                    <el-button v-if="!row.hasDiagnosis" type="primary" size="small" round icon="EditPen"
                                        @click="startDiagnosis(row)">
                                        接诊
                                    </el-button>
                                    <el-button v-else type="info" plain size="small" round icon="View"
                                        @click="viewDiagnosis(row)">
                                        查看
                                    </el-button>
                                </template>
                            </el-table-column>

                            <template #empty>
                                <el-empty description="今日暂无待诊患者，请休息片刻" :image-size="120" />
                            </template>
                        </el-table>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <el-dialog v-model="diagnosisDialogVisible" title="填写诊疗记录" width="650px" destroy-on-close align-center
            class="diagnosis-dialog">
            <div class="patient-banner" v-if="currentPatient">
                <div class="banner-item">
                    <span class="label">患者：</span>
                    <span class="val">{{ currentPatient.patientName }}</span>
                </div>
                <div class="banner-item">
                    <span class="label">性别：</span>
                    <span class="val">{{ currentPatient.patientGender === 1 ? '男' : '女' }}</span>
                </div>
                <div class="banner-item">
                    <span class="label">年龄：</span>
                    <span class="val">{{ currentPatient.patientAge }}岁</span>
                </div>
            </div>

            <el-form ref="diagnosisFormRef" :model="diagnosisForm" label-position="top" class="diagnosis-form">
                <el-form-item label="主诉 / 症状" prop="symptom" required>
                    <el-input v-model="diagnosisForm.symptom" type="textarea" :rows="3" placeholder="患者自述症状及体征..." />
                </el-form-item>

                <el-form-item label="初步诊断" prop="diagnosisResult" required>
                    <el-input v-model="diagnosisForm.diagnosisResult" type="textarea" :rows="2"
                        placeholder="请输入医学诊断结果..." />
                </el-form-item>

                <el-form-item label="处方 / 医嘱" prop="prescription">
                    <el-input v-model="diagnosisForm.prescription" type="textarea" :rows="4"
                        placeholder="开具药品或诊疗建议..." />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="diagnosisDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitDiagnosis" :loading="submitting">
                    提交并完成
                </el-button>
            </template>
        </el-dialog>

        <ChangePassword v-model="showPasswordDialog" :on-submit="handleChangePassword" />
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useDoctorStore } from '@/stores/doctor';
import { doctorPortalApi } from '@/api/doctorPortal';
import type { TodayPatient, DiagnosisCreateDTO } from '@/types/doctor-portal';
import ChangePassword from '@/components/ChangePassword.vue';
// 引入图标
import {
    Monitor, SwitchButton, UserFilled, Lock, List, Calendar,
    Document, BellFilled, Refresh, User, Male, Female,
    Sunny, Moon, EditPen, View
} from '@element-plus/icons-vue';

const router = useRouter();
const doctorStore = useDoctorStore();

const loading = ref(false);
const todayPatients = ref<TodayPatient[]>([]);

// 计算待诊人数
const pendingCount = computed(() => {
    return todayPatients.value.filter(p => !p.hasDiagnosis).length;
});

// 计算时间段问候语
const timePeriod = computed(() => {
    const hour = new Date().getHours();
    if (hour < 12) return '上午';
    if (hour < 18) return '下午';
    return '晚上';
});

// 修改密码相关
const showPasswordDialog = ref(false);

const diagnosisDialogVisible = ref(false);
const submitting = ref(false);
const currentPatient = ref<TodayPatient | null>(null);

const diagnosisForm = reactive<DiagnosisCreateDTO>({
    appointmentId: 0,
    symptom: '',
    diagnosisResult: '',
    prescription: ''
});

// 获取今日待诊患者
const fetchTodayPatients = async () => {
    loading.value = true;
    try {
        const res = await doctorPortalApi.getTodayPatients();
        if (res.code === 200) {
            todayPatients.value = res.data;
        }
    } catch (error) {
        console.error('获取待诊患者失败', error);
    } finally {
        loading.value = false;
    }
};

// 开始诊疗
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

// 提交诊疗
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
        fetchTodayPatients();
    } catch (error) {
        console.error('提交失败', error);
    } finally {
        submitting.value = false;
    }
};

// 查看诊疗记录
const viewDiagnosis = async (patient: TodayPatient) => {
    router.push(`/doctor/diagnosis?appointmentId=${patient.appointmentId}`);
};

// 修改密码
const handleChangePassword = async (data: { oldPassword: string; newPassword: string }) => {
    await doctorPortalApi.updatePassword(data);
    // 修改成功后退出登录
    doctorStore.logout();
};

// 退出登录
const handleLogout = () => {
    ElMessageBox.confirm('确定要退出工作站吗？', '提示', { type: 'warning' }).then(() => {
        doctorStore.logout();
    });
};

onMounted(() => {
    fetchTodayPatients();
});
</script>

<style scoped lang="scss">
:deep(:root) {
    --el-color-primary: #7c3aed;
    /* 医生端使用紫色系主色 */
    --el-color-primary-light-9: #f3e8ff;
}

.doctor-home-container {
    min-height: 100vh;
    background-color: #f3f4f6;
    font-family: 'Inter', sans-serif;
}

/* 1. 顶部导航 */
.portal-header {
    background: white;
    height: 64px;
    border-bottom: 1px solid #e5e7eb;
    padding: 0 32px;
    position: sticky;
    top: 0;
    z-index: 100;

    .header-content {
        max-width: 1400px;
        margin: 0 auto;
        height: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .brand {
        display: flex;
        align-items: center;
        gap: 12px;

        .logo-box {
            width: 36px;
            height: 36px;
            background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
        }

        .titles {
            display: flex;
            flex-direction: column;
            line-height: 1.2;

            h1 {
                font-size: 18px;
                font-weight: 700;
                margin: 0;
                color: #1f2937;
            }

            .subtitle {
                font-size: 12px;
                color: #9ca3af;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
        }
    }

    .user-actions {
        display: flex;
        align-items: center;
        gap: 12px;

        .welcome-text {
            font-size: 14px;
            color: #6b7280;

            .doctor-name {
                font-weight: 600;
                color: #1f2937;
            }
        }
    }
}

.main-content {
    max-width: 1400px;
    margin: 24px auto;
    padding: 0 24px;
}

/* 2. 个人卡片 */
.profile-card {
    border: none;
    border-radius: 12px;
    margin-bottom: 24px;
    overflow: hidden;

    .card-bg {
        height: 80px;
        background: linear-gradient(120deg, #8b5cf6 0%, #c4b5fd 100%);
    }

    .card-content {
        padding: 0 24px 24px;
        text-align: center;
        position: relative;

        .avatar {
            border: 4px solid white;
            margin-top: -36px;
            background: white;
            color: #d1d5db;
        }

        .name {
            margin: 12px 0 6px;
            font-size: 20px;
            color: #1f2937;
        }

        .job-tag {
            background-color: #7c3aed;
            border-color: #7c3aed;
        }

        .info-grid {
            margin-top: 24px;
            background: #f9fafb;
            border-radius: 8px;
            padding: 16px;
            text-align: left;

            .info-item {
                display: flex;
                justify-content: space-between;
                font-size: 14px;
                margin-bottom: 8px;

                &:last-child {
                    margin-bottom: 0;
                }

                label {
                    color: #9ca3af;
                }

                span {
                    color: #4b5563;
                    font-weight: 500;
                }
            }
        }

        .pwd-btn {
            margin-top: 16px;
            font-size: 13px;
        }
    }
}

/* 3. 快捷磁贴 */
.section-label {
    font-size: 14px;
    font-weight: 600;
    color: #6b7280;
    margin-bottom: 12px;
    padding-left: 4px;
}

.quick-grid {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.quick-tile {
    background: white;
    padding: 20px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    gap: 16px;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid transparent;

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    }

    .icon-wrapper {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        flex-shrink: 0;
    }

    .text {
        h3 {
            margin: 0 0 4px;
            font-size: 16px;
            color: #1f2937;
        }

        p {
            margin: 0;
            font-size: 13px;
            color: #9ca3af;
        }
    }

    /* 颜色变体 */
    &.primary {
        .icon-wrapper {
            background: #f3e8ff;
            color: #7c3aed;
        }

        &:hover {
            border-color: #ddd6fe;
        }
    }

    &.success {
        .icon-wrapper {
            background: #dcfce7;
            color: #10b981;
        }

        &:hover {
            border-color: #bbf7d0;
        }
    }

    &.warning {
        .icon-wrapper {
            background: #ffedd5;
            color: #f59e0b;
        }

        &:hover {
            border-color: #fed7aa;
        }
    }
}

/* 4. 待诊列表 */
.list-card {
    border: none;
    border-radius: 12px;
    height: 100%;

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .header-left {
            display: flex;
            align-items: center;
            gap: 10px;

            .icon-box {
                width: 32px;
                height: 32px;
                background: #fee2e2;
                color: #ef4444;
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .title {
                font-size: 16px;
                font-weight: 600;
                color: #1f2937;
            }
        }
    }
}

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
            font-size: 14px;
        }

        .p-age {
            font-size: 12px;
            color: #6b7280;
            display: flex;
            align-items: center;
            gap: 4px;
            margin-top: 2px;
        }
    }
}

.shift-tag {
    display: flex;
    align-items: center;
    width: fit-content;

    .mr-1 {
        margin-right: 4px;
    }
}

/* 5. 弹窗美化 */
.patient-banner {
    background: #f3e8ff;
    padding: 12px 20px;
    border-radius: 8px;
    display: flex;
    gap: 30px;
    margin-bottom: 24px;

    .banner-item {
        font-size: 14px;

        .label {
            color: #6b7280;
        }

        .val {
            color: #1f2937;
            font-weight: 600;
        }
    }
}
</style>