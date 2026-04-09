<template>
    <div class="portal-container">
        <header class="portal-header">
            <div class="header-content">
                <div class="logo">
                    <el-icon class="logo-icon" :size="32" color="var(--el-color-primary)">
                        <FirstAidKit />
                    </el-icon>
                    <h1>智慧医疗平台 <span class="subtitle">Smart Medical Service</span></h1>
                </div>
                <div class="nav-buttons">
                    <el-dropdown v-if="isPatientLoggedIn" @command="handleUserCommand">
                        <el-button type="primary" plain round class="user-btn">
                            <el-icon class="el-icon--left">
                                <UserFilled />
                            </el-icon>
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
                    <el-divider direction="vertical" />
                    <el-button link type="info" @click="$router.push('/admin/login')">管理员</el-button>
                    <el-button link type="info" @click="$router.push('/doctor/login')">医生端</el-button>
                </div>
            </div>
        </header>

        <main class="portal-main">
            <section class="welcome-section">
                <el-carousel height="420px" indicator-position="outside" :interval="5000">
                    <el-carousel-item>
                        <div class="hero-banner bg-gradient-blue">
                            <div class="hero-text">
                                <h2>专业医疗，<br>触手可及</h2>
                                <p>连接顶尖专家，提供全流程的智慧诊疗服务。<br>告别繁琐排队，指尖轻点即可完成预约。</p>
                                <div class="hero-actions">
                                    <el-button v-if="!isPatientLoggedIn" type="primary" size="large" round
                                        class="cta-btn" @click="$router.push('/user/login')">
                                        立即预约挂号 <el-icon class="el-icon--right">
                                            <Right />
                                        </el-icon>
                                    </el-button>
                                    <el-button v-else type="primary" size="large" round class="cta-btn"
                                        @click="$router.push('/book')">
                                        开始预约 <el-icon class="el-icon--right">
                                            <Calendar />
                                        </el-icon>
                                    </el-button>
                                </div>
                            </div>
                            <div class="hero-visual">
                                <el-icon :size="200">
                                    <Monitor />
                                </el-icon>
                            </div>
                        </div>
                    </el-carousel-item>

                    <el-carousel-item>
                        <div class="hero-banner bg-gradient-teal">
                            <div class="hero-text">
                                <h2>汇聚名医，<br>守护健康</h2>
                                <p>资深专家团队坐诊，覆盖全科室诊疗需求。<br>为您和家人的健康提供最坚实的专业保障。</p>
                                <div class="hero-actions">
                                    <el-button plain size="large" round class="cta-btn-light" @click="scrollToDoctors">
                                        查看专家团队 <el-icon class="el-icon--right">
                                            <UserFilled />
                                        </el-icon>
                                    </el-button>
                                </div>
                            </div>
                            <div class="hero-visual">
                                <el-icon :size="200">
                                    <FirstAidKit />
                                </el-icon>
                            </div>
                        </div>
                    </el-carousel-item>

                    <el-carousel-item>
                        <div class="hero-banner bg-gradient-purple">
                            <div class="hero-text">
                                <h2>云端档案，<br>智慧随行</h2>
                                <p>电子病历永久保存，随时随地查阅诊疗记录。<br>让医疗数据成为您健康管理的得力助手。</p>
                                <div class="hero-actions">
                                    <el-button plain size="large" round class="cta-btn-light"
                                        @click="$router.push(isPatientLoggedIn ? '/user/home' : '/user/login')">
                                        我的健康档案 <el-icon class="el-icon--right">
                                            <Document />
                                        </el-icon>
                                    </el-button>
                                </div>
                            </div>
                            <div class="hero-visual">
                                <el-icon :size="200">
                                    <DataLine />
                                </el-icon>
                            </div>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </section>

            <section class="content-section">
                <div class="section-header">
                    <h2 class="title">重点科室 <span class="sub-title">Departments</span></h2>
                    <el-button link type="primary">查看全部 <el-icon>
                            <ArrowRight />
                        </el-icon></el-button>
                </div>

                <el-row :gutter="24">
                    <el-col :span="6" v-for="dept in departmentList" :key="dept.id" class="mb-4">
                        <div class="dept-card" @click="goToDeptDoctors(dept.id)">
                            <div class="card-icon-wrapper">
                                <el-icon :size="28">
                                    <FirstAidKit />
                                </el-icon>
                            </div>
                            <div class="card-info">
                                <h3>{{ dept.deptName }}</h3>
                                <p>{{ dept.deptDesc || '提供专业的诊疗服务与健康咨询' }}</p>
                            </div>
                            <div class="hover-arrow">
                                <el-icon>
                                    <Right />
                                </el-icon>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </section>

            <section class="content-section doctor-section-bg" id="doctor-section">
                <div class="section-header">
                    <h2 class="title">专家团队 <span class="sub-title">Specialists</span></h2>
                </div>
                <el-row :gutter="24">
                    <el-col :span="6" v-for="doctor in doctorList" :key="doctor.id" class="mb-4">
                        <el-card :body-style="{ padding: '0px' }" shadow="hover" class="doctor-card">
                            <div class="doctor-header">
                                <el-avatar :size="80" class="doctor-avatar"
                                    src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png">
                                    <el-icon>
                                        <UserFilled />
                                    </el-icon>
                                </el-avatar>
                            </div>
                            <div class="doctor-body">
                                <h3>{{ doctor.name }}</h3>
                                <div class="tags">
                                    <el-tag size="small" effect="plain" type="primary">{{ doctor.jobTitle }}</el-tag>
                                    <el-tag size="small" effect="light" type="info">{{ doctor.deptName }}</el-tag>
                                </div>
                                <p class="intro">{{ doctor.introduction || '暂无详细简介，请点击查看详情。' }}</p>
                                <el-button class="book-btn" type="primary" plain block
                                    @click="goToBookDoctor(doctor.id)">
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
                <div class="footer-col">
                    <h4>智慧医疗平台</h4>
                    <p>致力于提供高效、便捷的数字化医疗服务体验。</p>
                </div>
                <div class="footer-col">
                    <h4>联系我们</h4>
                    <p>电话：400-123-4567</p>
                    <p>邮箱：support@hospital.com</p>
                </div>
            </div>
            <div class="copyright">
                <p>© 2025 Smart Medical Platform. All rights reserved.</p>
            </div>
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
    Right, Calendar, Monitor, DataLine, Document
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

const scrollToDoctors = () => {
    document.getElementById('doctor-section')?.scrollIntoView({ behavior: 'smooth' });
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
    --text-main: #1f2937;
    --text-secondary: #6b7280;
    --bg-light: #f3f4f6;
}

.portal-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background-color: #f8fafc;
    font-family: 'Inter', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.portal-header {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    padding: 0;
    height: 70px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    position: sticky;
    top: 0;
    z-index: 100;

    .header-content {
        max-width: 1280px;
        margin: 0 auto;
        height: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 24px;
    }

    .logo {
        display: flex;
        align-items: center;
        gap: 12px;

        .logo-icon {
            background: #eff6ff;
            padding: 8px;
            border-radius: 8px;
            box-sizing: content-box;
        }

        h1 {
            color: #1e293b;
            margin: 0;
            font-size: 22px;
            font-weight: 700;
            display: flex;
            flex-direction: column;
            line-height: 1.2;

            .subtitle {
                font-size: 12px;
                color: #94a3b8;
                font-weight: 400;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
        }
    }

    .nav-buttons {
        display: flex;
        gap: 16px;
        align-items: center;
    }
}

.portal-main {
    flex: 1;
    width: 100%;
    padding: 0;
}

/* 轮播图样式升级 */
.welcome-section {
    background: white;
    margin-bottom: 40px;

    .hero-banner {
        max-width: 1280px;
        margin: 0 auto;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 60px;
        border-radius: 20px;
        margin-top: 20px;
        color: white;
        position: relative;
        overflow: hidden;

        /* 预设三种渐变背景 */
        &.bg-gradient-blue {
            background: linear-gradient(120deg, #2563eb 0%, #06b6d4 100%);
        }

        &.bg-gradient-teal {
            background: linear-gradient(120deg, #0d9488 0%, #14b8a6 100%);
        }

        &.bg-gradient-purple {
            background: linear-gradient(120deg, #7c3aed 0%, #a855f7 100%);
        }
    }

    .hero-text {
        z-index: 2;
        max-width: 600px;

        h2 {
            font-size: 52px;
            line-height: 1.2;
            margin-bottom: 24px;
            font-weight: 800;
            text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        p {
            font-size: 20px;
            margin-bottom: 36px;
            opacity: 0.95;
            line-height: 1.6;
            font-weight: 400;
        }
    }

    .hero-visual {
        opacity: 0.15;
        transform: rotate(-10deg) scale(1.4);
        margin-right: -20px;
        filter: drop-shadow(0 0 20px rgba(255, 255, 255, 0.3));
    }

    .hero-actions {
        display: flex;
        gap: 16px;
    }

    .cta-btn {
        padding: 24px 36px;
        font-size: 16px;
        font-weight: 600;
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
        border: none;
    }

    .cta-btn-light {
        background: rgba(255, 255, 255, 0.15);
        border: 1px solid rgba(255, 255, 255, 0.4);
        color: white;
        padding: 24px 36px;
        font-size: 16px;
        backdrop-filter: blur(5px);

        &:hover {
            background: rgba(255, 255, 255, 0.25);
            border-color: white;
        }
    }
}

.content-section {
    max-width: 1280px;
    margin: 0 auto 60px;
    padding: 0 24px;

    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        margin-bottom: 30px;
        border-bottom: 1px solid #e5e7eb;
        padding-bottom: 15px;

        .title {
            font-size: 28px;
            color: #1e293b;
            margin: 0;
            display: flex;
            align-items: baseline;
            gap: 10px;

            .sub-title {
                font-size: 16px;
                color: #94a3b8;
                font-weight: 400;
            }
        }
    }
}

.mb-4 {
    margin-bottom: 24px;
}

.dept-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    border: 1px solid #e2e8f0;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    cursor: pointer;
    transition: all 0.3s ease;
    height: 100%;
    position: relative;
    overflow: hidden;

    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
        border-color: var(--el-color-primary-light-5);

        .card-icon-wrapper {
            background: var(--el-color-primary);
            color: white;
        }

        .hover-arrow {
            opacity: 1;
            transform: translateX(0);
        }
    }

    .card-icon-wrapper {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        background: #eff6ff;
        color: var(--el-color-primary);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 16px;
        transition: all 0.3s ease;
    }

    .card-info {
        h3 {
            margin: 0 0 8px;
            font-size: 18px;
            color: #1e293b;
        }

        p {
            color: #64748b;
            font-size: 14px;
            line-height: 1.5;
            margin: 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
    }

    .hover-arrow {
        position: absolute;
        bottom: 20px;
        right: 20px;
        opacity: 0;
        transform: translateX(-10px);
        transition: all 0.3s ease;
        color: var(--el-color-primary);
    }
}

.doctor-card {
    border: none;
    border-radius: 16px;
    overflow: hidden;
    transition: all 0.3s;
    height: 100%;
    border: 1px solid transparent;

    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
        border-color: #e2e8f0;
    }

    .doctor-header {
        background: linear-gradient(to bottom, #f1f5f9 50%, white 50%);
        padding: 20px 0 0;
        text-align: center;
    }

    .doctor-avatar {
        border: 4px solid white;
        background: white;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    }

    .doctor-body {
        padding: 15px 20px 20px;
        text-align: center;

        h3 {
            margin: 10px 0;
            font-size: 18px;
            color: #1e293b;
        }

        .tags {
            display: flex;
            justify-content: center;
            gap: 8px;
            margin-bottom: 12px;
        }

        .intro {
            color: #64748b;
            font-size: 13px;
            line-height: 1.6;
            height: 42px;
            overflow: hidden;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            margin-bottom: 16px;
        }

        .book-btn {
            border-radius: 8px;
            font-weight: 500;
        }
    }
}

.portal-footer {
    background: #1e293b;
    color: #94a3b8;
    padding: 60px 0 20px;
    margin-top: auto;

    .footer-content {
        max-width: 1280px;
        margin: 0 auto;
        padding: 0 24px;
        display: flex;
        gap: 60px;
        margin-bottom: 40px;

        h4 {
            color: white;
            font-size: 16px;
            margin-bottom: 20px;
        }

        p {
            font-size: 14px;
            margin-bottom: 10px;
        }
    }

    .copyright {
        text-align: center;
        border-top: 1px solid #334155;
        padding-top: 20px;
        font-size: 13px;
    }
}
</style>