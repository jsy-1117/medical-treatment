<template>
    <div class="user-home-container">
        <el-header class="user-header">
            <div class="header-inner">
                <div class="brand">
                    <el-icon :size="24" class="brand-icon">
                        <FirstAidKit />
                    </el-icon>
                    <h1>个人中心 <span class="subtitle">Patient Portal</span></h1>
                </div>
                <el-button type="primary" plain round icon="HomeFilled" @click="$router.push('/')">
                    返回首页
                </el-button>
            </div>
        </el-header>

        <div class="main-content">
            <el-row :gutter="24">
                <el-col :span="7">
                    <el-card class="profile-card" :body-style="{ padding: '0' }">
                        <div class="profile-header-bg"></div>
                        <div class="profile-content">
                            <div class="avatar-wrapper">
                                <el-avatar :size="88" class="user-avatar" icon="UserFilled" />
                                <div class="edit-badge" @click="openEditDialog" title="编辑资料">
                                    <el-icon>
                                        <Edit />
                                    </el-icon>
                                </div>
                            </div>

                            <h2 class="user-name">
                                {{ patientStore.patientInfo?.name || '未设置昵称' }}
                                <el-tag size="small" effect="plain" round v-if="patientStore.patientInfo?.age">
                                    {{ patientStore.patientInfo?.age }}岁
                                </el-tag>
                            </h2>
                            <p class="user-username">@{{ patientStore.patientInfo?.username }}</p>

                            <el-alert v-if="!isProfileComplete" title="请完善信息以便挂号" type="warning" show-icon
                                :closable="false" class="profile-alert" />

                            <div class="info-list">
                                <div class="info-item">
                                    <span class="label"><el-icon>
                                            <Iphone />
                                        </el-icon> 手机号</span>
                                    <span class="value" :class="{ 'missing': !patientStore.patientInfo?.phone }">
                                        {{ patientStore.patientInfo?.phone || '未绑定' }}
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="label"><el-icon>
                                            <Male />
                                        </el-icon> 性别</span>
                                    <span class="value" :class="{ 'missing': !patientStore.patientInfo?.gender }">
                                        {{ genderText }}
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="label"><el-icon>
                                            <Postcard />
                                        </el-icon> 身份证</span>
                                    <span class="value id-card">
                                        {{ patientStore.patientInfo?.idCard || '未实名' }}
                                    </span>
                                </div>
                            </div>

                            <div class="profile-actions">
                                <el-button icon="Key" @click="showPasswordDialog = true" block plain>修改密码</el-button>
                                <el-button icon="SwitchButton" type="danger" plain @click="handleLogout"
                                    block>退出登录</el-button>
                            </div>
                        </div>
                    </el-card>
                </el-col>

                <el-col :span="17">
                    <div class="section-title">我的诊疗概览</div>
                    <el-row :gutter="20" class="mb-4">
                        <el-col :span="8">
                            <div class="stat-card blue">
                                <div class="stat-icon"><el-icon>
                                        <Timer />
                                    </el-icon></div>
                                <div class="stat-info">
                                    <div class="stat-num">{{ stats.pending }}</div>
                                    <div class="stat-label">待就诊</div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="8">
                            <div class="stat-card green">
                                <div class="stat-icon"><el-icon>
                                        <CircleCheckFilled />
                                    </el-icon></div>
                                <div class="stat-info">
                                    <div class="stat-num">{{ stats.completed }}</div>
                                    <div class="stat-label">已完成</div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="8">
                            <div class="stat-card gray">
                                <div class="stat-icon"><el-icon>
                                        <CircleCloseFilled />
                                    </el-icon></div>
                                <div class="stat-info">
                                    <div class="stat-num">{{ stats.cancelled }}</div>
                                    <div class="stat-label">已取消</div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row :gutter="20" class="mb-4">
                        <el-col :span="12">
                            <div class="quick-action-card primary" @click="$router.push('/book')">
                                <div class="content">
                                    <h3>预约挂号</h3>
                                    <p>查找专家，在线预约</p>
                                </div>
                                <el-icon class="bg-icon">
                                    <Calendar />
                                </el-icon>
                                <div class="action-btn"><el-icon>
                                        <Right />
                                    </el-icon></div>
                            </div>
                        </el-col>
                        <el-col :span="12">
                            <div class="quick-action-card success" @click="$router.push('/user/appointments')">
                                <div class="content">
                                    <h3>我的挂号单</h3>
                                    <p>查看记录，管理预约</p>
                                </div>
                                <el-icon class="bg-icon">
                                    <Tickets />
                                </el-icon>
                                <div class="action-btn"><el-icon>
                                        <Right />
                                    </el-icon></div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-card class="recent-card" shadow="hover">
                        <template #header>
                            <div class="card-header">
                                <span class="title-with-icon"><el-icon>
                                        <Clock />
                                    </el-icon> 最近挂号记录</span>
                                <el-button type="primary" link @click="$router.push('/user/appointments')">
                                    查看全部 <el-icon class="el-icon--right">
                                        <ArrowRight />
                                    </el-icon>
                                </el-button>
                            </div>
                        </template>

                        <el-table :data="recentAppointments" v-loading="loading" style="width: 100%"
                            :header-cell-style="{ background: '#f8fafc' }">
                            <el-table-column label="医生信息" min-width="140">
                                <template #default="{ row }">
                                    <div class="doctor-cell">
                                        <el-avatar :size="32" src="" icon="UserFilled" class="mr-2" />
                                        <span class="doctor-name">{{ row.doctorName || `医生${row.doctorId}` }}</span>
                                    </div>
                                </template>
                            </el-table-column>

                            <el-table-column prop="workDate" label="就诊时间" width="140">
                                <template #default="{ row }">
                                    <div>{{ row.workDate }}</div>
                                    <el-tag size="small" type="info" effect="plain">{{ row.shiftType === 1 ? '上午' : '下午'
                                        }}</el-tag>
                                </template>
                            </el-table-column>

                            <el-table-column label="状态" width="100">
                                <template #default="{ row }">
                                    <el-tag :type="getStatusType(row.status)" effect="light" round>
                                        {{ getStatusText(row.status) }}
                                    </el-tag>
                                </template>
                            </el-table-column>

                            <el-table-column label="操作" width="100" align="right">
                                <template #default="{ row }">
                                    <el-button v-if="row.status === 0" type="danger" plain size="small" round
                                        @click="handleCancel(row)">
                                        取消
                                    </el-button>
                                    <span v-else class="text-gray">-</span>
                                </template>
                            </el-table-column>

                            <template #empty>
                                <el-empty description="暂无近期挂号记录" :image-size="80" />
                            </template>
                        </el-table>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <el-dialog v-model="editDialogVisible" title="编辑个人资料" width="500px" destroy-on-close align-center>
            <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px" class="custom-form">
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="editForm.name" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="editForm.gender">
                        <el-radio :value="1">男</el-radio>
                        <el-radio :value="2">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                    <el-input-number v-model="editForm.age" :min="1" :max="150" controls-position="right"
                        style="width: 100%" />
                </el-form-item>
                <el-form-item label="身份证" prop="idCard">
                    <el-input v-model="editForm.idCard" placeholder="请输入身份证号" maxlength="18" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存更改</el-button>
            </template>
        </el-dialog>

        <ChangePassword v-model="showPasswordDialog" :on-submit="handleChangePassword" />
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { usePatientStore } from '@/stores/patient';
import { appointmentApi } from '@/api/appointment';
import { patientAuthApi } from '@/api/patientAuth';
import type { AppointmentVO } from '@/types/appointment';
import type { PatientUpdateDTO } from '@/types/patient';
import ChangePassword from '@/components/ChangePassword.vue';
// 引入图标库
import {
    UserFilled, Edit, Iphone, Male, Postcard, Key, SwitchButton,
    HomeFilled, FirstAidKit, Timer, CircleCheckFilled, CircleCloseFilled,
    Calendar, Right, Tickets, Clock, ArrowRight
} from '@element-plus/icons-vue';

const patientStore = usePatientStore();

const loading = ref(false);
const recentAppointments = ref<AppointmentVO[]>([]);

// 修改密码相关
const showPasswordDialog = ref(false);

// 编辑相关
const editDialogVisible = ref(false);
const saving = ref(false);
const editFormRef = ref<FormInstance>();

const editForm = reactive<PatientUpdateDTO>({
    name: '',
    phone: '',
    gender: undefined,
    age: undefined,
    idCard: ''
});

const editRules: FormRules = {
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ],
    gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
    age: [{ required: true, message: '请输入年龄', trigger: 'blur' }]
};

// 检查信息是否完整
const isProfileComplete = computed(() => patientStore.isProfileComplete());

// 挂号统计
const stats = ref({
    pending: 0,
    completed: 0,
    cancelled: 0
});

// 性别文本
const genderText = computed(() => {
    const gender = patientStore.patientInfo?.gender;
    if (gender === 1) return '男';
    if (gender === 2) return '女';
    return '未设置';
});

// 打开编辑对话框
const openEditDialog = () => {
    if (patientStore.patientInfo) {
        editForm.name = patientStore.patientInfo.name || '';
        editForm.phone = patientStore.patientInfo.phone || '';
        editForm.gender = patientStore.patientInfo.gender;
        editForm.age = patientStore.patientInfo.age;
        editForm.idCard = patientStore.patientInfo.idCard || '';
    }
    editDialogVisible.value = true;
};

// 保存个人信息
const handleSaveProfile = async () => {
    if (!editFormRef.value) return;

    await editFormRef.value.validate(async (valid) => {
        if (!valid) return;

        saving.value = true;
        try {
            await patientStore.updateProfile(editForm);
            editDialogVisible.value = false;
            ElMessage.success('个人信息保存成功');
        } catch (error) {
            console.error('保存失败', error);
        } finally {
            saving.value = false;
        }
    });
};

// 修改密码
const handleChangePassword = async (data: { oldPassword: string; newPassword: string }) => {
    await patientAuthApi.updatePassword(data);
    patientStore.logout();
};

const getStatusType = (status: number) => {
    switch (status) {
        case 0: return 'primary'; // 待就诊改为蓝色更友好
        case 1: return 'success';
        case 2: return 'info'; // 取消改为灰色
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

// 获取挂号数据
const fetchAppointments = async () => {
    if (!patientStore.patientInfo?.id) return;
    loading.value = true;
    try {
        const res = await appointmentApi.getPatientAppointments(patientStore.patientInfo.id, 1, 5);
        if (res.code === 200) {
            recentAppointments.value = res.data.records;

            // 计算统计数据
            const allRes = await appointmentApi.getPatientAppointments(patientStore.patientInfo.id, 1, 1000);
            if (allRes.code === 200) {
                const all = allRes.data.records;
                stats.value.pending = all.filter((a: AppointmentVO) => a.status === 0).length;
                stats.value.completed = all.filter((a: AppointmentVO) => a.status === 1).length;
                stats.value.cancelled = all.filter((a: AppointmentVO) => a.status === 2).length;
            }
        }
    } catch (error) {
        console.error('获取挂号记录失败', error);
    } finally {
        loading.value = false;
    }
};

// 取消挂号
const handleCancel = (row: AppointmentVO) => {
    ElMessageBox.confirm('确定取消此挂号？此操作不可恢复。', '取消挂号', {
        type: 'warning',
        confirmButtonText: '确定取消',
        cancelButtonText: '暂不取消'
    }).then(async () => {
        try {
            await appointmentApi.cancel(row.id, patientStore.patientInfo!.id);
            ElMessage.success('挂号已成功取消');
            fetchAppointments();
        } catch (error) {
            console.error('取消失败', error);
        }
    });
};

// 退出登录
const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
        patientStore.logout();
    });
};

onMounted(() => {
    fetchAppointments();
});
</script>

<style scoped lang="scss">
:deep(:root) {
    --el-color-primary: #3b82f6;
}

.user-home-container {
    min-height: 100vh;
    background-color: #f8fafc;
    font-family: 'Inter', sans-serif;
}

/* 顶部导航 */
.user-header {
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

.section-title {
    font-size: 16px;
    font-weight: 600;
    color: #334155;
    margin-bottom: 16px;
    padding-left: 4px;
}

.mb-4 {
    margin-bottom: 24px;
}

.mr-2 {
    margin-right: 8px;
}

/* 左侧：个人信息卡片 */
.profile-card {
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    overflow: hidden;

    .profile-header-bg {
        height: 100px;
        background: linear-gradient(120deg, #3b82f6 0%, #2563eb 100%);
    }

    .profile-content {
        padding: 0 24px 24px;
        position: relative;
        text-align: center;

        .avatar-wrapper {
            position: relative;
            display: inline-block;
            margin-top: -44px;
            margin-bottom: 12px;

            .user-avatar {
                border: 4px solid #fff;
                background: #fff;
                color: #cbd5e1;
            }

            .edit-badge {
                position: absolute;
                bottom: 0;
                right: 0;
                background: #3b82f6;
                color: white;
                width: 28px;
                height: 28px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                border: 2px solid white;
                cursor: pointer;
                transition: transform 0.2s;

                &:hover {
                    transform: scale(1.1);
                    background: #2563eb;
                }
            }
        }

        .user-name {
            margin: 0 0 4px;
            font-size: 20px;
            color: #1e293b;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }

        .user-username {
            margin: 0 0 20px;
            color: #94a3b8;
            font-size: 14px;
        }

        .profile-alert {
            margin-bottom: 20px;
            background-color: #fff7ed;
        }

        .info-list {
            margin-bottom: 24px;

            .info-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 12px 0;
                border-bottom: 1px solid #f1f5f9;
                font-size: 14px;

                &:last-child {
                    border-bottom: none;
                }

                .label {
                    color: #64748b;
                    display: flex;
                    align-items: center;
                    gap: 6px;
                }

                .value {
                    color: #334155;
                    font-weight: 500;

                    &.missing {
                        color: #ef4444;
                    }
                }
            }
        }

        .profile-actions {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }
    }
}

/* 统计卡片 */
.stat-card {
    background: white;
    padding: 20px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
    border: 1px solid #f1f5f9;

    .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
    }

    .stat-info {
        .stat-num {
            font-size: 24px;
            font-weight: 700;
            color: #1e293b;
            line-height: 1.2;
        }

        .stat-label {
            font-size: 13px;
            color: #64748b;
        }
    }

    &.blue .stat-icon {
        background: #eff6ff;
        color: #3b82f6;
    }

    &.green .stat-icon {
        background: #f0fdf4;
        color: #22c55e;
    }

    &.gray .stat-icon {
        background: #f1f5f9;
        color: #64748b;
    }
}

/* 快捷操作卡片 */
.quick-action-card {
    background: white;
    padding: 24px;
    border-radius: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    border: 1px solid #e2e8f0;
    transition: all 0.3s ease;

    .content {
        z-index: 2;

        h3 {
            margin: 0 0 6px;
            font-size: 18px;
            color: #1e293b;
        }

        p {
            margin: 0;
            font-size: 13px;
            color: #64748b;
        }
    }

    .action-btn {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: #f8fafc;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #cbd5e1;
        transition: all 0.3s;
        z-index: 2;
    }

    .bg-icon {
        position: absolute;
        right: -10px;
        bottom: -20px;
        font-size: 100px;
        opacity: 0.05;
        transform: rotate(-15deg);
        z-index: 1;
    }

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.05);

        .action-btn {
            background: #3b82f6;
            color: white;
        }
    }

    &.primary:hover {
        border-color: #bfdbfe;
    }

    &.success:hover {
        border-color: #bbf7d0;

        .action-btn {
            background: #22c55e;
        }
    }
}

/* 最近挂号卡片 */
.recent-card {
    border: none;
    border-radius: 12px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title-with-icon {
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            color: #1e293b;
            font-size: 16px;
        }
    }

    .doctor-cell {
        display: flex;
        align-items: center;

        .doctor-name {
            font-weight: 500;
            color: #334155;
        }
    }

    .text-gray {
        color: #cbd5e1;
    }
}

@media (max-width: 768px) {
    .profile-card {
        margin-bottom: 20px;
    }
}
</style>