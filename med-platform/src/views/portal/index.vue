<template>
    <div class="portal-root">
        <header class="portal-header">
            <div class="header-inner">
                <div class="brand" @click="$router.push('/')" style="cursor:pointer">
                    <div class="brand-mark">+</div>
                    <span>智慧医疗平台</span>
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
            <!-- Hero 欢迎区 -->
            <section class="hero-section">
                <div class="hero-card">
                    <div class="hero-text">
                        <span class="hero-eyebrow">智能医疗平台</span>
                        <h1>智医云智慧医疗平台</h1>
                        <p>连接优质医疗资源，守护您的健康生活。点击下方按钮，快速完成在线预约。</p>
                        <div class="hero-actions">
                            <el-button v-if="!isPatientLoggedIn" type="primary" size="large" round class="hero-btn"
                                @click="$router.push('/user/login')">
                                立即登录
                                <el-icon class="el-icon--right"><Right /></el-icon>
                            </el-button>
                            <el-button v-else type="primary" size="large" round class="hero-btn"
                                @click="$router.push('/book')">
                                预约挂号
                                <el-icon class="el-icon--right"><Right /></el-icon>
                            </el-button>
                            <el-button plain size="large" round class="hero-btn-secondary"
                                @click="$router.push(isPatientLoggedIn ? '/user/home' : '/user/login')">
                                {{ isPatientLoggedIn ? '进入个人中心' : '查看健康档案' }}
                            </el-button>
                        </div>
                    </div>
                    <div class="hero-stats">
                        <div class="hero-stat">
                            <span class="stat-num">{{ departmentList.length }}</span>
                            <span class="stat-label">科室</span>
                        </div>
                        <div class="hero-stat">
                            <span class="stat-num">{{ doctorList.length }}</span>
                            <span class="stat-label">专家</span>
                        </div>
                        <div class="hero-note">
                            <el-icon><Calendar /></el-icon>
                            <span>现有科室与相关专家数量</span>
                        </div>
                    </div>
                </div>
            </section>

            <!-- 重点科室 -->
            <section class="content-section">
                <div class="section-header">
                    <h2>重点科室 <span class="sub">Departments</span></h2>
                    <el-button link type="primary" @click="$router.push('/book')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
                </div>
                <div class="dept-grid">
                    <div v-for="dept in departmentList" :key="dept.id" class="dept-card" @click="goToDeptDoctors(dept.id)">
                        <div class="dept-icon"><FirstAidKit /></div>
                        <div class="dept-info">
                            <h3>{{ dept.deptName }}</h3>
                            <p>{{ dept.deptDesc || '提供专业诊疗与健康咨询' }}</p>
                        </div>
                        <el-icon class="dept-arrow"><Right /></el-icon>
                    </div>
                </div>
            </section>

            <!-- 专家团队 -->
            <section class="content-section doctor-section" id="doctor-section">
                <div class="section-header">
                    <h2>专家团队 <span class="sub">Specialists</span></h2>
                </div>
                <div class="doctor-grid">
                    <div v-for="doctor in doctorList" :key="doctor.id" class="doctor-card">
                        <div class="doctor-body">
                            <el-avatar :size="56" class="doctor-avatar" icon="UserFilled"
                                src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f6epng.png" />
                            <h3>{{ doctor.name }}</h3>
                            <div class="doctor-tags">
                                <span class="tag tag-primary">{{ doctor.jobTitle }}</span>
                                <span class="tag tag-info">{{ doctor.deptName }}</span>
                            </div>
                            <p class="doctor-intro">{{ doctor.introduction || '暂无详细简介，请点击预约了解更多。' }}</p>
                            <el-button class="doctor-book-btn" type="primary" plain @click="goToBookDoctor(doctor.id)">
                                预约挂号
                            </el-button>
                        </div>
                    </div>
                </div>
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
.portal-root {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: #f7f8f5;
    color: #1a2a2a;
    font-family: 'DM Sans', 'Noto Sans SC', -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
    -webkit-font-smoothing: antialiased;
    line-height: 1.5;
}

.portal-header {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(247, 248, 245, 0.88);
    backdrop-filter: blur(16px);
    border-bottom: 1px solid #eef0ec;

    .header-inner {
        max-width: 1060px;
        margin: 0 auto;
        height: 60px;
        padding: 0 28px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
    }

    .brand {
        display: flex;
        align-items: center;
        gap: 10px;

        .brand-mark {
            width: 30px;
            height: 30px;
            border-radius: 8px;
            background: linear-gradient(135deg, #1a8a7a, #2db8a0);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 15px;
            font-weight: 700;
        }

        span {
            font-size: 16px;
            font-weight: 600;
            letter-spacing: -0.01em;
            color: #1a2a2a;
        }
    }

    .nav-buttons {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;
    }
}

.portal-main {
    flex: 1;
    width: 100%;
    padding: 0 0 56px;
}

/* ===== Hero ===== */
.hero-section {
    max-width: 1060px;
    margin: 28px auto 0;
    padding: 0 28px;
}

.hero-card {
    background: linear-gradient(135deg, #1a8a7a 0%, #2db8a0 100%);
    border-radius: 20px;
    padding: 40px 40px 36px;
    color: white;
    display: flex;
    justify-content: space-between;
    gap: 32px;
    flex-wrap: wrap;
}

.hero-text {
    flex: 1;
    min-width: 260px;

    .hero-eyebrow {
        display: inline-flex;
        padding: 5px 12px;
        border-radius: 20px;
        background: rgba(255, 255, 255, 0.18);
        font-size: 12px;
        font-weight: 600;
        letter-spacing: 0.03em;
        margin-bottom: 14px;
    }

    h1 {
        margin: 0 0 10px;
        font-size: 30px;
        font-weight: 700;
        letter-spacing: -0.01em;
        line-height: 1.2;
    }

    p {
        margin: 0 0 22px;
        font-size: 14px;
        opacity: 0.85;
        line-height: 1.6;
        max-width: 420px;
    }
}

.hero-actions {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

.hero-btn,
.hero-btn-secondary {
    min-width: 140px;
    font-weight: 500;
}

.hero-btn-secondary {
    background: rgba(255, 255, 255, 0.95);
    border-color: transparent;
    color: #1a2a2a;

    &:hover {
        background: white;
        border-color: transparent;
    }
}

.hero-stats {
    display: flex;
    flex-direction: column;
    gap: 10px;
    min-width: 160px;
}

.hero-stat {
    background: rgba(255, 255, 255, 0.12);
    border-radius: 14px;
    padding: 14px 18px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .stat-num {
        font-size: 26px;
        font-weight: 700;
        line-height: 1;
    }

    .stat-label {
        font-size: 13px;
        opacity: 0.8;
    }
}

.hero-note {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 18px;
    border-radius: 14px;
    background: rgba(0, 0, 0, 0.15);
    font-size: 13px;
    line-height: 1.5;
}

/* ===== Content sections ===== */
.content-section {
    max-width: 1060px;
    margin: 40px auto 0;
    padding: 0 28px;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 18px;

    h2 {
        margin: 0;
        font-size: 22px;
        font-weight: 650;
        color: #1a2a2a;

        .sub {
            font-size: 13px;
            color: #9aabab;
            font-weight: 500;
        }
    }
}

/* ===== Department card grid ===== */
.dept-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
}

.dept-card {
    flex: 1 1 calc(25% - 9px);
    min-width: 180px;
    background: white;
    border-radius: 14px;
    padding: 22px 20px;
    cursor: pointer;
    transition: all 0.25s;
    display: flex;
    flex-direction: column;
    gap: 12px;
    box-shadow: 0 2px 10px rgba(26, 138, 122, 0.06);

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 28px rgba(26, 138, 122, 0.12);
    }

    .dept-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: #e6f4f1;
        color: #1a8a7a;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
    }

    .dept-info {
        flex: 1;

        h3 {
            margin: 0 0 6px;
            font-size: 16px;
            font-weight: 600;
            color: #1a2a2a;
        }

        p {
            margin: 0;
            font-size: 13px;
            color: #5a6a6a;
            line-height: 1.5;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
    }

    .dept-arrow {
        align-self: flex-end;
        color: #d0d8d5;
        font-size: 14px;
        transition: all 0.2s;
    }

    &:hover .dept-arrow {
        color: #1a8a7a;
        transform: translateX(2px);
    }
}

/* ===== Doctor card grid ===== */
.doctor-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
}

.doctor-card {
    flex: 1 1 calc(25% - 9px);
    min-width: 200px;
    background: white;
    border-radius: 14px;
    overflow: hidden;
    transition: all 0.25s;
    box-shadow: 0 2px 10px rgba(26, 138, 122, 0.06);

    &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 28px rgba(26, 138, 122, 0.12);
    }
}

.doctor-body {
    padding: 24px 18px 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;

    .doctor-avatar {
        background: #f0f2ef;
        color: #bcc5c2;
        border: 4px solid #f7f8f5;
        box-shadow: 0 8px 20px rgba(26, 138, 122, 0.06);
        margin-bottom: 14px;
    }

    h3 {
        margin: 0 0 10px;
        font-size: 16px;
        font-weight: 600;
        color: #1a2a2a;
    }
}

.doctor-tags {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
    justify-content: center;
    margin-bottom: 12px;
}

.tag {
    display: inline-flex;
    padding: 3px 10px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 500;
}

.tag-primary {
    background: #e6f4f1;
    color: #1a8a7a;
}

.tag-info {
    background: #eef0ec;
    color: #5a6a6a;
}

.doctor-intro {
    margin: 0 0 16px;
    color: #5a6a6a;
    font-size: 13px;
    line-height: 1.6;
    min-height: 42px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.doctor-book-btn {
    border-radius: 10px;
    font-weight: 500;
    font-size: 13px;
}

/* ===== Footer ===== */
.portal-footer {
    margin-top: auto;
    padding: 36px 28px 24px;
    background: #1a2a2a;
    color: #9aabab;

    .footer-content {
        max-width: 1060px;
        margin: 0 auto 20px;
        display: flex;
        justify-content: space-between;
        gap: 24px;
        flex-wrap: wrap;

        h4 {
            margin: 0 0 10px;
            color: white;
            font-size: 15px;
        }

        p {
            margin: 0 0 8px;
            font-size: 13px;
        }
    }

    .copyright {
        max-width: 1060px;
        margin: 0 auto;
        padding-top: 16px;
        border-top: 1px solid rgba(154, 171, 171, 0.2);
        text-align: center;
        font-size: 13px;
    }
}

@media (max-width: 900px) {
    .dept-card { flex: 1 1 calc(50% - 6px); }
    .doctor-card { flex: 1 1 calc(50% - 6px); }
}

@media (max-width: 640px) {
    .dept-card { flex: 1 1 100%; }
    .doctor-card { flex: 1 1 100%; }
    .hero-card { padding: 28px 24px; flex-direction: column; }
    .hero-text h1 { font-size: 24px; }
    .portal-header .header-inner { padding: 12px 16px; flex-direction: column; align-items: flex-start; }
    .nav-buttons { width: 100%; }
    .content-section { padding: 0 16px; }
    .hero-section { padding: 0 16px; }
}
</style>
