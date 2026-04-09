<template>
    <div class="admin-container">
        <el-container class="layout-container">
            <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-sidebar"
                @mouseenter="handleSidebarHover(false)" @mouseleave="handleSidebarHover(true)">
                <div class="sidebar-logo">
                    <el-icon :size="24" color="#fff">
                        <Monitor />
                    </el-icon>
                    <span v-show="!isCollapse" class="logo-text">医疗管理后台</span>
                </div>

                <el-menu active-text-color="#409EFF" background-color="#001529" class="el-menu-vertical"
                    text-color="#a6adb4" :default-active="activeMenu" :collapse="isCollapse"
                    :collapse-transition="false" style="border-right: none;">
                    <el-menu-item index="dashboard">
                        <el-icon>
                            <Odometer />
                        </el-icon>
                        <template #title>控制台首页</template>
                    </el-menu-item>

                    <el-sub-menu index="user-mgmt">
                        <template #title>
                            <el-icon>
                                <User />
                            </el-icon>
                            <span>人员管理</span>
                        </template>
                        <el-menu-item index="patient" @click="goToPatient">用户管理</el-menu-item>
                        <el-menu-item index="doctor" @click="goToDoctor">医生管理</el-menu-item>
                    </el-sub-menu>

                    <el-menu-item index="department" @click="goToDepartment">
                        <el-icon>
                            <OfficeBuilding />
                        </el-icon>
                        <template #title>科室管理</template>
                    </el-menu-item>

                    <el-menu-item index="schedule" @click="goToSchedule">
                        <el-icon>
                            <Calendar />
                        </el-icon>
                        <template #title>排班管理</template>
                    </el-menu-item>

                    <el-menu-item index="appointment" @click="goToAppointment">
                        <el-icon>
                            <Tickets />
                        </el-icon>
                        <template #title>挂号管理</template>
                    </el-menu-item>
                </el-menu>
            </el-aside>

            <el-container>
                <el-header class="admin-header">
                    <div class="header-left">
                        <el-breadcrumb separator="/">
                            <el-breadcrumb-item>首页</el-breadcrumb-item>
                            <el-breadcrumb-item>工作台</el-breadcrumb-item>
                        </el-breadcrumb>
                    </div>

                    <div class="header-right">
                        <el-tooltip content="刷新数据" effect="dark" placement="bottom">
                            <el-icon class="action-icon" @click="fetchDashboardData">
                                <Refresh />
                            </el-icon>
                        </el-tooltip>
                        <el-tooltip content="全屏" effect="dark" placement="bottom">
                            <el-icon class="action-icon">
                                <FullScreen />
                            </el-icon>
                        </el-tooltip>

                        <el-dropdown trigger="click" @command="handleUserCommand">
                            <span class="el-dropdown-link">
                                <el-avatar :size="32" class="user-avatar" icon="UserFilled" />
                                <span class="username">{{ adminStore.adminInfo?.name || 'Admin' }}</span>
                                <el-icon class="el-icon--right"><arrow-down /></el-icon>
                            </span>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                                    <el-dropdown-item command="password">修改密码</el-dropdown-item>
                                    <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </el-header>

                <el-main class="admin-main">
                    <el-row :gutter="20" class="stat-row">
                        <el-col :span="6">
                            <div class="stat-card">
                                <div class="stat-icon blue-bg"><el-icon>
                                        <UserFilled />
                                    </el-icon></div>
                                <div class="stat-info">
                                    <div class="label">总用户数</div>
                                    <div class="value">{{ statistics.patientCount }}</div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="stat-card">
                                <div class="stat-icon green-bg"><el-icon>
                                        <FirstAidKit />
                                    </el-icon></div>
                                <div class="stat-info">
                                    <div class="label">在职医生</div>
                                    <div class="value">{{ statistics.doctorCount }}</div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="stat-card">
                                <div class="stat-icon orange-bg"><el-icon>
                                        <Tickets />
                                    </el-icon></div>
                                <div class="stat-info">
                                    <div class="label">今日挂号</div>
                                    <div class="value">{{ statistics.todayAppointmentCount }}</div>
                                </div>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="stat-card">
                                <div class="stat-icon purple-bg"><el-icon>
                                        <Money />
                                    </el-icon></div>
                                <div class="stat-info">
                                    <div class="label">总科室数</div>
                                    <div class="value">{{ statistics.deptCount }}</div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row :gutter="20" class="dashboard-content">
                        <el-col :span="16">
                            <el-card shadow="never" class="panel-card">
                                <template #header>
                                    <div class="card-header">
                                        <span>快捷管理</span>
                                        <el-tag type="info" size="small">常用功能</el-tag>
                                    </div>
                                </template>
                                <div class="quick-actions-wrapper">
                                    <div class="quick-action-item" @click="goToDoctor">
                                        <div class="content">
                                            <h3>医生管理</h3>
                                            <p>审核医生入职</p>
                                            <el-button type="primary" size="small" round>立即进入</el-button>
                                        </div>
                                        <el-icon class="icon">
                                            <FirstAidKit />
                                        </el-icon>
                                    </div>

                                    <div class="quick-action-item secondary" @click="goToAppointment">
                                        <div class="content">
                                            <h3>挂号管理</h3>
                                            <p>查看挂号详情</p>
                                            <el-button type="success" size="small" round>立即查看</el-button>
                                        </div>
                                        <el-icon class="icon">
                                            <List />
                                        </el-icon>
                                    </div>
                                </div>
                            </el-card>
                        </el-col>

                        <el-col :span="8">
                            <el-card shadow="never" class="panel-card">
                                <template #header>
                                    <div class="card-header">
                                        <span>管理员信息</span>
                                    </div>
                                </template>
                                <div class="admin-profile">
                                    <el-avatar :size="64"
                                        src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                                    <h3>{{ adminStore.adminInfo?.name }}</h3>
                                    <p class="role">超级管理员</p>
                                    <div class="info-list">
                                        <div class="info-item">
                                            <span class="label">账号</span>
                                            <span class="val">{{ adminStore.adminInfo?.username }}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="label">当前时间</span>
                                            <span class="val">{{ currentDate }}</span>
                                        </div>
                                    </div>
                                    <el-divider />
                                    <el-button type="primary" plain block @click="goToProfile">编辑资料</el-button>
                                </div>
                            </el-card>
                        </el-col>
                    </el-row>
                </el-main>
            </el-container>
        </el-container>

        <ChangePassword v-model="showPasswordDialog" :on-submit="handleChangePassword" />
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAdminStore } from '@/stores/admin';
import { ElMessageBox, ElMessage } from 'element-plus';
import ChangePassword from '@/components/ChangePassword.vue';
import { adminApi } from '@/api/admin';
import { patientApi } from '@/api/patient';
import { doctorApi } from '@/api/doctor';
import { departmentApi } from '@/api/department';
import { appointmentApi } from '@/api/appointment';
import dayjs from 'dayjs';

// 引入图标
import {
    Monitor, Odometer, User, OfficeBuilding, Calendar, Tickets,
    FullScreen, Refresh, UserFilled, ArrowDown, FirstAidKit, List, Money
} from '@element-plus/icons-vue';

const router = useRouter();
const adminStore = useAdminStore();

const showPasswordDialog = ref(false);

// 侧边栏状态控制
// 默认收起 (true)，鼠标移入变为 false
const isCollapse = ref(true);

// 统计数据对象
const statistics = ref({
    patientCount: 0,
    doctorCount: 0,
    todayAppointmentCount: 0,
    deptCount: 0
});

const currentDate = computed(() => dayjs().format('YYYY-MM-DD'));
const activeMenu = ref('dashboard');

// 侧边栏鼠标交互
const handleSidebarHover = (collapse: boolean) => {
    isCollapse.value = collapse;
};

// 获取真实数据
const fetchDashboardData = async () => {
    console.log('=== 开始获取数据 ===');
    console.log('Admin Token:', localStorage.getItem('admin_token'));
    console.log('Current Path:', window.location.pathname);

    try {
        // 1. 获取患者总数
        console.log('正在请求患者数据...');
        const patientRes = await patientApi.getList({ pageNum: 1, pageSize: 1 });
        console.log('患者数据响应:', patientRes);
        if (patientRes.code === 200) statistics.value.patientCount = patientRes.data.total;

        // 2. 获取医生总数
        console.log('正在请求医生数据...');
        const doctorRes = await doctorApi.getList({ pageNum: 1, pageSize: 1 });
        console.log('医生数据响应:', doctorRes);
        if (doctorRes.code === 200) statistics.value.doctorCount = doctorRes.data.total;

        // 3. 获取科室总数
        console.log('正在请求科室数据...');
        const deptRes = await departmentApi.getList({ pageNum: 1, pageSize: 1 });
        console.log('科室数据响应:', deptRes);
        if (deptRes.code === 200) statistics.value.deptCount = deptRes.data.total;

        // 4. 获取今日挂号数
        console.log('正在请求挂号数据...');
        const today = dayjs().format('YYYY-MM-DD');
        const apptRes = await appointmentApi.getList({
            pageNum: 1,
            pageSize: 1,
            startDate: today,
            endDate: today
        });
        console.log('挂号数据响应:', apptRes);
        if (apptRes.code === 200) statistics.value.todayAppointmentCount = apptRes.data.total;

        console.log('=== 数据获取完成 ===');
        console.log('最终统计:', statistics.value);

    } catch (error) {
        console.error("❌ 获取首页统计数据失败", error);
    }
};

const goToProfile = () => router.push('/admin/profile');
const goToPatient = () => router.push('/admin/patient');
const goToDoctor = () => router.push('/admin/doctor');
const goToAppointment = () => router.push('/admin/appointment');
const goToDepartment = () => router.push('/admin/department');
const goToSchedule = () => router.push('/admin/schedule');

// 修改密码
const handleChangePassword = async (data: { oldPassword: string; newPassword: string }) => {
    try {
        await adminApi.updatePassword(data);
        ElMessage.success('密码修改成功，请重新登录');
        adminStore.logout();
    } catch (error) {
        // api 内部通常处理了错误提示
    }
};

const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        adminStore.logout();
    });
};

const handleUserCommand = (command: string) => {
    if (command === 'logout') handleLogout();
    if (command === 'password') showPasswordDialog.value = true;
    if (command === 'profile') goToProfile();
};

onMounted(() => {
    fetchDashboardData();
});
</script>

<style scoped lang="scss">
.admin-container {
    height: 100vh;
    display: flex;
    background-color: #f0f2f5;
    font-family: 'Inter', sans-serif;
}

.layout-container {
    width: 100%;
    height: 100%;
}

/* 侧边栏样式 - 增加过渡动画 */
.admin-sidebar {
    background-color: #001529;
    color: white;
    /* 核心：宽度变化的过渡动画 */
    transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
    display: flex;
    flex-direction: column;
    overflow-x: hidden;
    box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
    z-index: 20;
    /* 确保层级高于内容区，展开时覆盖在内容上还是推开内容取决于布局模式，这里是推开 */

    .sidebar-logo {
        height: 64px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        background-color: #002140;
        font-size: 18px;
        font-weight: 600;
        color: white;
        white-space: nowrap;
        overflow: hidden;
        transition: all 0.3s;

        .logo-text {
            animation: fadeIn 0.5s;
        }
    }

    .el-menu-vertical {
        border-right: none;
        flex: 1;

        /* 隐藏收起时可能出现的文字溢出 */
        &:not(.el-menu--collapse) {
            width: 220px;
        }
    }
}

/* 顶部 Header */
.admin-header {
    background: #fff;
    height: 64px;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    z-index: 10;

    .header-left {
        display: flex;
        align-items: center;
    }

    .header-right {
        display: flex;
        align-items: center;
        gap: 20px;

        .action-icon {
            font-size: 20px;
            cursor: pointer;
            color: #595959;

            &:hover {
                color: #409EFF;
            }
        }

        .el-dropdown-link {
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;
            color: #595959;

            .username {
                font-weight: 500;
            }
        }
    }
}

/* 主内容区 */
.admin-main {
    background-color: #f5f7fa;
    padding: 20px;
    overflow-y: auto;
}

/* 1. 统计卡片样式 */
.stat-row {
    margin-bottom: 20px;
}

.stat-card {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 15px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
    transition: transform 0.3s;
    height: 100px;
    box-sizing: border-box;

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }

    .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        color: #fff;
        flex-shrink: 0;

        &.blue-bg {
            background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
        }

        &.green-bg {
            background: linear-gradient(135deg, #52c41a 0%, #95de64 100%);
        }

        &.orange-bg {
            background: linear-gradient(135deg, #fa8c16 0%, #ffd666 100%);
        }

        &.purple-bg {
            background: linear-gradient(135deg, #722ed1 0%, #b37feb 100%);
        }
    }

    .stat-info {
        overflow: hidden;

        .label {
            color: #8c8c8c;
            font-size: 14px;
            margin-bottom: 8px;
        }

        .value {
            color: #262626;
            font-size: 28px;
            font-weight: 700;
            line-height: 1;
        }
    }
}

/* 2. 快捷入口 & 信息卡片 */
.panel-card {
    border: none;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
    height: 100%;

    :deep(.el-card__header) {
        border-bottom: 1px solid #f0f0f0;
        padding: 15px 20px;
    }

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: 600;
        font-size: 16px;
    }
}

.quick-actions-wrapper {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}

.quick-action-item {
    background: linear-gradient(135deg, #e6f7ff 0%, #ffffff 100%);
    border: 1px solid #bae7ff;
    border-radius: 12px;
    padding: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;
    height: 120px;
    box-sizing: border-box;

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(24, 144, 255, 0.15);
        border-color: #409eff;
    }

    &.secondary {
        background: linear-gradient(135deg, #f6ffed 0%, #ffffff 100%);
        border-color: #d9f7be;

        .content p {
            color: #5b8c00;
        }

        .icon {
            color: #95de64;
            opacity: 0.3;
        }

        &:hover {
            box-shadow: 0 6px 16px rgba(82, 196, 26, 0.15);
            border-color: #52c41a;
        }
    }

    .content {
        z-index: 2;

        h3 {
            margin: 0 0 8px;
            font-size: 18px;
            color: #1f1f1f;
        }

        p {
            margin: 0 0 16px;
            font-size: 13px;
            color: #096dd9;
            opacity: 0.8;
        }
    }

    .icon {
        font-size: 64px;
        position: absolute;
        right: -10px;
        bottom: -10px;
        color: #409eff;
        opacity: 0.15;
        transform: rotate(-15deg);
        z-index: 1;
    }
}

/* 管理员信息侧边 */
.admin-profile {
    text-align: center;
    padding: 10px 0;

    h3 {
        margin: 10px 0 5px;
        font-size: 18px;
        color: #262626;
    }

    .role {
        color: #8c8c8c;
        font-size: 13px;
        margin-bottom: 20px;
    }

    .info-list {
        text-align: left;
        background: #f9f9f9;
        padding: 15px;
        border-radius: 8px;
        margin-bottom: 20px;

        .info-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            font-size: 14px;

            &:last-child {
                margin-bottom: 0;
            }

            .label {
                color: #8c8c8c;
            }

            .val {
                color: #262626;
                font-weight: 500;
            }
        }
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}
</style>