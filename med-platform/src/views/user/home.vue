<template>
    <PatientLayout>
        <!-- 欢迎条 -->
        <div class="welcome-card">
            <div>
                <h1>{{ patientStore.patientInfo?.name ? `${timePeriod}好，${patientStore.patientInfo.name}` : '欢迎回来' }}</h1>
                <p v-if="stats.pending > 0">您有 {{ stats.pending }} 条待就诊的挂号记录，请按时前往科室就诊。</p>
                <p v-else>管理您的个人资料与就诊记录。</p>
            </div>
            <div class="welcome-stats">
                <div><span class="num">{{ stats.pending }}</span><span class="label">待就诊</span></div>
                <div><span class="num">{{ stats.completed }}</span><span class="label">已完成</span></div>
                <div><span class="num">{{ stats.cancelled }}</span><span class="label">已取消</span></div>
            </div>
        </div>

        <!-- 快速入口 -->
        <div class="quick-grid">
            <div class="quick-card" @click="$router.push('/book')">
                <div class="quick-icon" style="background:#e6f4f1;color:#1a8a7a"><el-icon size="22"><Calendar /></el-icon></div>
                <h4>预约挂号</h4>
                <p>查找专家，在线预约</p>
            </div>
            <div class="quick-card" @click="$router.push('/user/appointments')">
                <div class="quick-icon" style="background:#fef3c7;color:#b45309"><el-icon size="22"><Tickets /></el-icon></div>
                <h4>我的挂号</h4>
                <p>查看记录，管理预约</p>
            </div>
            <div class="quick-card" @click="openEditDialog">
                <div class="quick-icon" style="background:#e0eefc;color:#1d6fc7"><el-icon size="22"><Edit /></el-icon></div>
                <h4>编辑资料</h4>
                <p>更新个人信息</p>
            </div>
            <div class="quick-card" @click="showPasswordDialog = true">
                <div class="quick-icon" style="background:#f3e8ff;color:#7c3aed"><el-icon size="22"><Lock /></el-icon></div>
                <h4>修改密码</h4>
                <p>保障账号安全</p>
            </div>
        </div>

        <!-- 内容区 -->
        <div class="profile-grid">
            <!-- 个人资料 -->
            <div class="profile-card">
                <h3 class="card-label">个人资料</h3>
                <div class="profile-avatar-row">
                    <el-avatar :size="64" class="profile-avatar" icon="UserFilled" />
                    <div>
                        <div class="profile-name">
                            {{ patientStore.patientInfo?.name || '未设置昵称' }}
                            <el-tag size="small" effect="plain" round v-if="patientStore.patientInfo?.age">{{ patientStore.patientInfo.age }}岁</el-tag>
                        </div>
                        <div class="profile-username">@{{ patientStore.patientInfo?.username }}</div>
                    </div>
                </div>

                <el-alert v-if="!isProfileComplete" title="请完善信息以便挂号" type="warning" show-icon :closable="false" class="profile-alert" />

                <div class="profile-info-list">
                    <div class="info-row">
                        <span class="info-label"><el-icon><Iphone /></el-icon> 手机号</span>
                        <span class="info-value" :class="{ missing: !patientStore.patientInfo?.phone }">{{ patientStore.patientInfo?.phone || '未绑定' }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label"><el-icon><Male /></el-icon> 性别</span>
                        <span class="info-value" :class="{ missing: !patientStore.patientInfo?.gender }">{{ genderText }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label"><el-icon><Postcard /></el-icon> 身份证</span>
                        <span class="info-value">{{ patientStore.patientInfo?.idCard || '未实名' }}</span>
                    </div>
                </div>

                <div class="profile-actions">
                    <el-button size="small" plain @click="openEditDialog">编辑资料</el-button>
                    <el-button size="small" plain type="danger" @click="handleLogout">退出登录</el-button>
                </div>
            </div>

            <!-- 最近挂号记录 -->
            <div class="profile-card">
                <h3 class="card-label">最近挂号</h3>
                <div class="tl-list" v-loading="loading">
                    <div v-for="item in recentAppointments" :key="item.id" class="tl-item" :class="statusClass(item.status)">
                        <div class="tl-dot"></div>
                        <div class="tl-body">
                            <div class="tl-date">{{ item.workDate }} {{ item.shiftType === 1 ? '上午' : '下午' }}</div>
                            <div class="tl-doctor">{{ item.doctorName || `医生${item.doctorId}` }}</div>
                            <div class="tl-meta">
                                <span class="tl-tag" :class="statusTagClass(item.status)">{{ statusText(item.status) }}</span>
                                <span class="tl-dept">{{ item.deptName }}</span>
                            </div>
                            <el-button v-if="item.status === 0" size="small" round plain type="danger" @click="handleCancel(item)">取消</el-button>
                        </div>
                    </div>
                    <el-empty v-if="!loading && recentAppointments.length === 0" description="暂无挂号记录" :image-size="80" />
                </div>
                <el-button link type="primary" size="small" @click="$router.push('/user/appointments')" style="margin-top:12px">
                    查看全部挂号 <el-icon><ArrowRight /></el-icon>
                </el-button>
            </div>
        </div>

        <!-- 编辑资料弹窗 -->
        <el-dialog v-model="editDialogVisible" title="编辑个人资料" width="500px" destroy-on-close align-center>
            <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px">
                <el-form-item label="姓名" prop="name"><el-input v-model="editForm.name" placeholder="请输入真实姓名" /></el-form-item>
                <el-form-item label="手机号" prop="phone"><el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" /></el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="editForm.gender">
                        <el-radio :value="1">男</el-radio>
                        <el-radio :value="2">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄" prop="age"><el-input-number v-model="editForm.age" :min="1" :max="150" controls-position="right" style="width:100%" /></el-form-item>
                <el-form-item label="身份证" prop="idCard"><el-input v-model="editForm.idCard" placeholder="请输入身份证号" maxlength="18" /></el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存更改</el-button>
            </template>
        </el-dialog>

        <ChangePassword v-model="showPasswordDialog" :on-submit="handleChangePassword" />
    </PatientLayout>
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
import PatientLayout from '@/components/PatientLayout.vue';
import ChangePassword from '@/components/ChangePassword.vue';
import {
    Calendar, Tickets, Edit, Lock, UserFilled, Iphone, Male,
    Postcard, ArrowRight
} from '@element-plus/icons-vue';

const patientStore = usePatientStore();

const loading = ref(false);
const recentAppointments = ref<AppointmentVO[]>([]);
const showPasswordDialog = ref(false);
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

const timePeriod = computed(() => {
    const h = new Date().getHours();
    if (h < 12) return '早上';
    if (h < 14) return '中午';
    if (h < 18) return '下午';
    return '晚上';
});

const isProfileComplete = computed(() => patientStore.isProfileComplete());

const stats = ref({ pending: 0, completed: 0, cancelled: 0 });

const genderText = computed(() => {
    const g = patientStore.patientInfo?.gender;
    if (g === 1) return '男';
    if (g === 2) return '女';
    return '未设置';
});

const statusClass = (s: number) => s === 1 ? 'tl-done' : s === 2 ? 'tl-cancel' : '';
const statusText = (s: number) => ['待就诊', '已完成', '已取消'][s] || '未知';
const statusTagClass = (s: number) => ['tag-pending', 'tag-done', 'tag-cancel'][s] || '';

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

const handleChangePassword = async (data: { oldPassword: string; newPassword: string }) => {
    await patientAuthApi.updatePassword(data);
    patientStore.logout();
};

const fetchAppointments = async () => {
    if (!patientStore.patientInfo?.id) return;
    loading.value = true;
    try {
        const res = await appointmentApi.getPatientAppointments(patientStore.patientInfo.id, 1, 5);
        if (res.code === 200) {
            recentAppointments.value = res.data.records;
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
.welcome-card {
    background: linear-gradient(135deg, #1a8a7a 0%, #2db8a0 100%);
    border-radius: 20px;
    padding: 28px 32px;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
    flex-wrap: wrap;
    margin-bottom: 24px;

    h1 {
        margin: 0 0 6px;
        font-size: 22px;
        font-weight: 600;
        letter-spacing: -0.01em;
    }

    p {
        margin: 0;
        font-size: 14px;
        opacity: 0.85;
        line-height: 1.6;
    }
}

.welcome-stats {
    display: flex;
    gap: 24px;

    div {
        text-align: center;
    }

    .num {
        display: block;
        font-size: 26px;
        font-weight: 700;
        line-height: 1.2;
    }

    .label {
        font-size: 12px;
        opacity: 0.75;
    }
}

.quick-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
    margin-bottom: 28px;
}

.quick-card {
    background: white;
    border-radius: 14px;
    padding: 20px 16px;
    text-align: center;
    cursor: pointer;
    transition: all 0.25s;
    box-shadow: 0 2px 10px rgba(26, 138, 122, 0.06);

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 24px rgba(26, 138, 122, 0.1);
    }

    .quick-icon {
        width: 44px;
        height: 44px;
        border-radius: 12px;
        margin: 0 auto 10px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    h4 {
        margin: 0 0 4px;
        font-size: 14px;
        font-weight: 600;
        color: #1a2a2a;
    }

    p {
        margin: 0;
        font-size: 12px;
        color: #5a6a6a;
    }
}

.profile-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.profile-card {
    background: white;
    border-radius: 14px;
    padding: 24px;
    box-shadow: 0 2px 10px rgba(26, 138, 122, 0.06);
}

.card-label {
    font-size: 13px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: #9aabab;
    margin: 0 0 18px;
}

.profile-avatar-row {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 18px;

    .profile-avatar {
        background: #f0f2ef;
        color: #bcc5c2;
        border: 3px solid #f7f8f5;
        box-shadow: 0 4px 12px rgba(26, 138, 122, 0.08);
    }

    .profile-name {
        font-size: 17px;
        font-weight: 600;
        color: #1a2a2a;
        display: flex;
        align-items: center;
        gap: 6px;
        margin-bottom: 2px;
    }

    .profile-username {
        font-size: 13px;
        color: #9aabab;
    }
}

.profile-alert {
    margin-bottom: 16px;
}

.profile-info-list {
    margin-bottom: 18px;
}

.info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #eef0ec;
    font-size: 14px;

    &:last-child {
        border-bottom: none;
    }

    .info-label {
        color: #5a6a6a;
        display: flex;
        align-items: center;
        gap: 6px;
    }

    .info-value {
        color: #1a2a2a;
        font-weight: 500;

        &.missing {
            color: #e74c3c;
        }
    }
}

.profile-actions {
    display: flex;
    gap: 10px;
}

.tl-list {
    min-height: 120px;
}

.tl-item {
    position: relative;
    padding: 0 0 18px 28px;

    &:last-child {
        padding-bottom: 0;
    }

    &::before {
        content: '';
        position: absolute;
        left: 8px;
        top: 18px;
        bottom: 0;
        width: 2px;
        background: #dee5e2;
    }

    &:last-child::before {
        display: none;
    }
}

.tl-dot {
    position: absolute;
    left: 2px;
    top: 4px;
    width: 14px;
    height: 14px;
    border-radius: 50%;
    background: #1a8a7a;
    border: 3px solid #e6f4f1;
    z-index: 1;
}

.tl-done .tl-dot {
    background: #9aabab;
    border-color: #eef0ec;
}

.tl-cancel .tl-dot {
    background: #e74c3c;
    border-color: #fde8e8;
}

.tl-body {
    padding-left: 8px;
}

.tl-date {
    font-size: 12px;
    color: #9aabab;
    margin-bottom: 2px;
}

.tl-doctor {
    font-size: 14px;
    font-weight: 600;
    color: #1a2a2a;
    margin-bottom: 4px;
}

.tl-meta {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 8px;
}

.tl-tag {
    display: inline-flex;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 11px;
    font-weight: 500;
}

.tag-pending {
    background: #fef3c7;
    color: #b45309;
}

.tag-done {
    background: #e6f4f1;
    color: #1a8a7a;
}

.tag-cancel {
    background: #fee2e2;
    color: #dc2626;
}

.tl-dept {
    font-size: 12px;
    color: #5a6a6a;
}

@media (max-width: 768px) {
    .quick-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .profile-grid {
        grid-template-columns: 1fr;
    }

    .welcome-stats {
        width: 100%;
        justify-content: space-around;
    }
}
</style>
