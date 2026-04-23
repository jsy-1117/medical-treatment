<template>
    <div class="portal-container">
        <header class="portal-header">
            <div class="header-content">
                <div class="brand">
                    <el-icon class="brand-icon" :size="28" color="var(--el-color-primary)">
                        <FirstAidKit />
                    </el-icon>
                    <div class="brand-text">
                        <h1>智慧医疗平台</h1>
                        <p>Smart Medical Service</p>
                    </div>
                </div>

                <div class="nav-buttons">
                    <el-dropdown v-if="isPatientLoggedIn" @command="handleUserCommand">
                        <el-button class="user-btn" type="primary" plain round>
                            <el-icon class="el-icon--left"><UserFilled /></el-icon>
                            {{ patientStore.patientInfo?.name }}
                            <el-icon class="el-icon--right"><arrow-down /></el-icon>
                        </el-button>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="center">个人中心</el-dropdown-item>
                                <el-dropdown-item command="appointments">我的挂号</el-dropdown-item>
                                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                    <template v-else>
                        <el-button text @click="$router.push('/user/login')">用户登录</el-button>
                        <el-button type="primary" round @click="$router.push('/user/register')">注册账号</el-button>
                    </template>
                    <el-button link type="info" @click="$router.push('/admin/login')">管理员</el-button>
                    <el-button link type="info" @click="$router.push('/doctor/login')">医生端</el-button>
                </div>
            </div>
        </header>

        <main class="portal-main">
            <section class="hero-section">
                <div class="hero-copy">
                    <span class="eyebrow">Intelligent Medical Platform</span>
                    <h2>智医云智慧医疗平台</h2>
                    <p>
                        点击下方按钮进行挂号
                    </p>

                    <div class="hero-actions">
                        <el-button
                            v-if="!isPatientLoggedIn"
                            type="primary"
                            size="large"
                            round
                            class="primary-action"
                            @click="$router.push('/user/login')"
                        >
                            立即登录
                            <el-icon class="el-icon--right"><Right /></el-icon>
                        </el-button>
                        <el-button
                            v-else
                            type="primary"
                            size="large"
                            round
                            class="primary-action"
                            @click="$router.push('/book')"
                        >
                            预约挂号
                            <el-icon class="el-icon--right"><Right /></el-icon>
                        </el-button>
                        <el-button
                            plain
                            size="large"
                            round
                            class="secondary-action"
                            @click="$router.push(isPatientLoggedIn ? '/user/home' : '/user/login')"
                        >
                            {{ isPatientLoggedIn ? '进入个人中心' : '查看健康档案' }}
                        </el-button>
                    </div>
                </div>

                <div class="hero-panel">
                    <div class="hero-metric">
                        <div class="metric-value">{{ departmentList.length }}</div>
                        <div class="metric-label">科室</div>
                    </div>
                    <div class="hero-metric highlight">
                        <div class="metric-value">{{ doctorList.length }}</div>
                        <div class="metric-label">专家</div>
                    </div>
                    <div class="hero-note">
                        <el-icon><Calendar /></el-icon>
                        <span>现有科室与相关专家数量</span>
                    </div>
                </div>
            </section>

            <section class="content-section">
                <div class="section-header">
                    <h2 class="title">重点科室 <span class="sub-title">Departments</span></h2>
                    <el-button link type="primary">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
                </div>

                <el-row :gutter="20">
                    <el-col :xs="12" :sm="12" :md="6" v-for="dept in departmentList" :key="dept.id" class="mb-4">
                        <div class="dept-card" @click="goToDeptDoctors(dept.id)">
                            <div class="card-top">
                                <div class="card-icon-wrapper">
                                    <el-icon :size="26"><FirstAidKit /></el-icon>
                                </div>
                                <el-icon class="card-arrow"><Right /></el-icon>
                            </div>
                            <div class="card-info">
                                <h3>{{ dept.deptName }}</h3>
                                <p>{{ dept.deptDesc || '提供专业诊疗与健康咨询' }}</p>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </section>

            <section class="content-section doctor-section-bg" id="doctor-section">
                <div class="section-header">
                    <h2 class="title">专家团队 <span class="sub-title">Specialists</span></h2>
                </div>
                <el-row :gutter="20">
                    <el-col :xs="24" :sm="12" :md="6" v-for="doctor in doctorList" :key="doctor.id" class="mb-4">
                        <el-card :body-style="{ padding: '0px' }" shadow="never" class="doctor-card">
                            <div class="doctor-body">
                                <el-avatar
                                    :size="56"
                                    class="doctor-avatar"
                                    src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
                                >
                                    <el-icon><UserFilled /></el-icon>
                                </el-avatar>
                                <h3>{{ doctor.name }}</h3>
                                <div class="tags">
                                    <el-tag size="small" effect="plain" type="primary">{{ doctor.jobTitle }}</el-tag>
                                    <el-tag size="small" effect="light" type="info">{{ doctor.deptName }}</el-tag>
                                </div>
                                <p class="intro">{{ doctor.introduction || '暂无详细简介，请点击预约了解更多。' }}</p>
                                <el-button class="book-btn" type="primary" plain @click="goToBookDoctor(doctor.id)">
                                    预约挂号
                                </el-button>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </section>
        </main>

        <footer class="portal-footer">
            <div class="footer-content">
                <div>
                    <h4>智慧医疗平台</h4>
                    <p>提供更轻、更清晰的患者端医疗体验。</p>
                </div>
                <div>
                    <h4>联系我们</h4>
                    <p>电话：400-123-4567</p>
                    <p>邮箱：support@hospital.com</p>
                </div>
            </div>
            <div class="copyright">© 2025 Smart Medical Platform. All rights reserved.</div>
        </footer>

        <ChatWindow />
    </div>
</template>

<script setup lang="ts">
import ChatWindow from '@/components/ChatWindow.vue';
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import {
    ArrowDown, ArrowRight, UserFilled, FirstAidKit,
    Right, Calendar
} from '@element-plus/icons-vue';
import { usePatientStore } from '@/stores/patient';
import { publicApi } from '@/api/public';
import type { DepartmentVO } from '@/types/department';
import type { DoctorPublicVO } from '@/types/public';

const router = useRouter();
const patientStore = usePatientStore();

const departmentList = ref<DepartmentVO[]>([]);
const doctorList = ref<DoctorPublicVO[]>([]);

const isPatientLoggedIn = computed(() => !!patientStore.token);

const fetchDepartments = async () => {
    try {
        const res = await publicApi.getDepartments();
        if (res.code === 200) {
            departmentList.value = res.data.slice(0, 8);
        }
    } catch (error) {
        console.error('获取科室列表失败', error);
    }
};

const fetchDoctors = async () => {
    try {
        const res = await publicApi.getDoctors({ pageNum: 1, pageSize: 8 });
        if (res.code === 200) {
            doctorList.value = res.data.records;
        }
    } catch (error) {
        console.error('获取医生列表失败', error);
    }
};

const goToDeptDoctors = (deptId: number) => {
    router.push(`/book?deptId=${deptId}`);
};

const goToBookDoctor = (doctorId: number) => {
    if (!isPatientLoggedIn.value) {
        router.push('/user/login');
        return;
    }
    router.push(`/book?doctorId=${doctorId}`);
};

const handleUserCommand = (command: string) => {
    switch (command) {
        case 'center':
            router.push('/user/home');
            break;
        case 'appointments':
            router.push('/user/appointments');
            break;
        case 'logout':
            patientStore.logout();
            break;
    }
};

onMounted(() => {
    fetchDepartments();
    fetchDoctors();
});
</script>

<style scoped lang="scss">
:deep(:root) {
    --primary-color: #3b82f6;
    --text-main: #0f172a;
    --text-secondary: #64748b;
    --surface: rgba(255, 255, 255, 0.9);
}

.portal-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background:
        radial-gradient(circle at top left, rgba(59, 130, 246, 0.08), transparent 30%),
        radial-gradient(circle at top right, rgba(16, 185, 129, 0.08), transparent 24%),
        #f8fafc;
    font-family: 'Inter', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.portal-header {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(255, 255, 255, 0.88);
    backdrop-filter: blur(14px);
    border-bottom: 1px solid #e2e8f0;

    .header-content {
        max-width: 1280px;
        margin: 0 auto;
        height: 72px;
        padding: 0 24px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 24px;
    }

    .brand {
        display: flex;
        align-items: center;
        gap: 12px;

        .brand-icon {
            background: #eff6ff;
            border-radius: 14px;
            padding: 8px;
            box-sizing: content-box;
        }

        .brand-text {
            h1 {
                margin: 0;
                font-size: 18px;
                font-weight: 700;
                color: #0f172a;
            }

            p {
                margin: 2px 0 0;
                color: #94a3b8;
                font-size: 12px;
                letter-spacing: 0.08em;
                text-transform: uppercase;
            }
        }
    }

    .nav-buttons {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;
    }
}

.portal-main {
    flex: 1;
    width: 100%;
    padding: 24px 0 56px;
}

.hero-section {
    max-width: 1280px;
    margin: 0 auto 40px;
    padding: 0 24px;
    display: grid;
    grid-template-columns: 1.6fr 0.9fr;
    gap: 20px;
}

.hero-copy,
.hero-panel {
    border: 1px solid #e2e8f0;
    border-radius: 24px;
    background: var(--surface);
    box-shadow: 0 20px 45px rgba(15, 23, 42, 0.04);
}

.hero-copy {
    padding: 36px;

    .eyebrow {
        display: inline-flex;
        align-items: center;
        padding: 6px 12px;
        border-radius: 999px;
        background: #eff6ff;
        color: #2563eb;
        font-size: 12px;
        font-weight: 600;
        letter-spacing: 0.04em;
        text-transform: uppercase;
        margin-bottom: 16px;
    }

    h2 {
        margin: 0 0 14px;
        font-size: 40px;
        line-height: 1.15;
        color: #0f172a;
        letter-spacing: -0.03em;
    }

    p {
        margin: 0;
        max-width: 680px;
        color: #475569;
        font-size: 16px;
        line-height: 1.75;
    }
}

.hero-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    margin-top: 24px;
}

.primary-action,
.secondary-action {
    min-width: 156px;
}

.secondary-action {
    border-color: #cbd5e1;
    color: #334155;
    background: rgba(255, 255, 255, 0.9);
}

.hero-panel {
    padding: 24px;
    display: grid;
    gap: 14px;
    align-content: center;
}

.hero-metric {
    padding: 18px 20px;
    border-radius: 18px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;

    &.highlight {
        background: linear-gradient(135deg, #eff6ff, #f0f9ff);
        border-color: #bfdbfe;
    }

    .metric-value {
        font-size: 32px;
        font-weight: 700;
        color: #0f172a;
        line-height: 1;
    }

    .metric-label {
        margin-top: 6px;
        color: #64748b;
        font-size: 13px;
    }
}

.hero-note {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 16px 18px;
    border-radius: 16px;
    background: #0f172a;
    color: #e2e8f0;
    font-size: 14px;
    line-height: 1.5;
}

.content-section {
    max-width: 1280px;
    margin: 0 auto 56px;
    padding: 0 24px;

    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        margin-bottom: 20px;

        .title {
            margin: 0;
            font-size: 24px;
            color: #0f172a;
            display: flex;
            align-items: baseline;
            gap: 10px;

            .sub-title {
                font-size: 14px;
                color: #94a3b8;
                font-weight: 500;
            }
        }
    }
}

.mb-4 {
    margin-bottom: 20px;
}

.dept-card {
    min-height: 160px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.96);
    border: 1px solid #e2e8f0;
    padding: 20px;
    cursor: pointer;
    transition: all 0.25s ease;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    &:hover {
        transform: translateY(-3px);
        border-color: #bfdbfe;
        box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
    }

    .card-top {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .card-icon-wrapper {
        width: 48px;
        height: 48px;
        border-radius: 14px;
        background: #eff6ff;
        color: #2563eb;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .card-arrow {
        color: #cbd5e1;
        transition: all 0.25s ease;
    }

    &:hover .card-arrow {
        color: #2563eb;
        transform: translateX(2px);
    }

    .card-info {
        h3 {
            margin: 20px 0 8px;
            font-size: 17px;
            color: #0f172a;
        }

        p {
            margin: 0;
            color: #64748b;
            font-size: 14px;
            line-height: 1.6;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
    }
}

.doctor-section-bg {
    .doctor-card {
        border-radius: 20px;
        border: 1px solid #e2e8f0;
        background: rgba(255, 255, 255, 0.96);
        overflow: hidden;
        transition: all 0.25s ease;
        height: 100%;

        &:hover {
            transform: translateY(-3px);
            box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
            border-color: #bfdbfe;
        }
    }

    .doctor-body {
        padding: 24px 18px 20px;
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
    }

    .doctor-avatar {
        background: #f8fafc;
        color: #cbd5e1;
        border: 4px solid #fff;
        box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
        margin-bottom: 14px;
    }

    h3 {
        margin: 0 0 10px;
        font-size: 17px;
        color: #0f172a;
    }

    .tags {
        display: flex;
        justify-content: center;
        gap: 8px;
        flex-wrap: wrap;
        margin-bottom: 14px;
    }

    .intro {
        margin: 0 0 16px;
        color: #64748b;
        font-size: 13px;
        line-height: 1.65;
        min-height: 42px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }

    .book-btn {
        border-radius: 10px;
        font-weight: 500;
    }
}

.portal-footer {
    margin-top: auto;
    padding: 36px 24px 24px;
    background: #0f172a;
    color: #94a3b8;

    .footer-content {
        max-width: 1280px;
        margin: 0 auto 20px;
        display: flex;
        justify-content: space-between;
        gap: 24px;
        flex-wrap: wrap;

        h4 {
            margin: 0 0 10px;
            color: #fff;
            font-size: 15px;
        }

        p {
            margin: 0 0 8px;
            font-size: 14px;
        }
    }

    .copyright {
        max-width: 1280px;
        margin: 0 auto;
        padding-top: 16px;
        border-top: 1px solid rgba(148, 163, 184, 0.2);
        text-align: center;
        font-size: 13px;
    }
}

@media (max-width: 1024px) {
    .hero-section {
        grid-template-columns: 1fr;
    }

    .hero-copy h2 {
        font-size: 32px;
    }
}

@media (max-width: 768px) {
    .portal-header .header-content {
        height: auto;
        padding: 16px 20px;
        align-items: flex-start;
        flex-direction: column;
    }

    .hero-section,
    .content-section {
        padding: 0 16px;
    }

    .hero-copy {
        padding: 24px;
    }

    .hero-copy h2 {
        font-size: 28px;
    }

    .nav-buttons {
        width: 100%;
    }
}
</style>