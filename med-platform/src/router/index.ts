import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  // ==========================================
  // 公开页面
  // ==========================================
  {
    path: '/',
    name: 'Portal',
    component: () => import('@/views/portal/index.vue'),
    meta: { title: '智慧医疗平台' }
  },

  // ==========================================
  // 用户端（患者）
  // ==========================================
  {
    path: '/user/login',
    name: 'UserLogin',
    component: () => import('@/views/user/login.vue'),
    meta: { title: '用户登录' }
  },
  {
    path: '/user/register',
    name: 'UserRegister',
    component: () => import('@/views/user/register.vue'),
    meta: { title: '用户注册' }
  },
  {
    path: '/user/home',
    name: 'UserHome',
    component: () => import('@/views/user/home.vue'),
    meta: { title: '个人中心', requiresPatient: true }
  },
  {
    path: '/user/appointments',
    name: 'UserAppointments',
    component: () => import('@/views/user/appointments.vue'),
    meta: { title: '我的挂号', requiresPatient: true }
  },
  {
    path: '/book',
    name: 'Book',
    component: () => import('@/views/user/book.vue'),
    meta: { title: '预约挂号', requiresPatient: true }
  },

  // ==========================================
  // 管理员端
  // ==========================================
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '管理员注册' }
  },
  {
    path: '/admin/home',
    name: 'AdminHome',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '管理后台', requiresAdmin: true }
  },
  {
    path: '/admin/profile',
    name: 'AdminProfile',
    component: () => import('@/views/admin/profile.vue'),
    meta: { title: '个人中心', requiresAdmin: true }
  },
  {
    path: '/admin/patient',
    name: 'PatientManage',
    component: () => import('@/views/patient/index.vue'),
    meta: { title: '用户管理', requiresAdmin: true }
  },
  {
    path: '/admin/doctor',
    name: 'DoctorManage',
    component: () => import('@/views/doctor/index.vue'),
    meta: { title: '医生管理', requiresAdmin: true }
  },
  {
    path: '/admin/appointment',
    name: 'AppointmentManage',
    component: () => import('@/views/appointment/index.vue'),
    meta: { title: '挂号管理', requiresAdmin: true }
  },
  {
    path: '/admin/department',
    name: 'DepartmentManage',
    component: () => import('@/views/department/index.vue'),
    meta: { title: '科室管理', requiresAdmin: true }
  },
  {
    path: '/admin/schedule',
    name: 'ScheduleManage',
    component: () => import('@/views/schedule/index.vue'),
    meta: { title: '排班管理', requiresAdmin: true }
  },
  // ==========================================
  // 医生端
  // ==========================================
  {
    path: '/doctor/login',
    name: 'DoctorLogin',
    component: () => import('@/views/doctor/login.vue'),
    meta: { title: '医生登录' }
  },
  {
    path: '/doctor/home',
    name: 'DoctorHome',
    component: () => import('@/views/doctor/home.vue'),
    meta: { title: '医生工作站', requiresDoctor: true }
  },
  {
    path: '/doctor/schedule',
    name: 'DoctorSchedule',
    component: () => import('@/views/doctor/schedule.vue'),
    meta: { title: '我的排班', requiresDoctor: true }
  },
  {
    path: '/doctor/patients',
    name: 'DoctorPatients',
    component: () => import('@/views/doctor/patients.vue'),
    meta: { title: '患者管理', requiresDoctor: true }
  },
  {
    path: '/doctor/diagnosis',
    name: 'DoctorDiagnosis',
    component: () => import('@/views/doctor/diagnosis.vue'),
    meta: { title: '诊疗记录', requiresDoctor: true }
  },

  // 兼容旧路由（重定向）
  { path: '/login', redirect: '/admin/login' },
  { path: '/register', redirect: '/admin/register' },
  { path: '/home', redirect: '/admin/home' }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const adminToken = localStorage.getItem('admin_token');
  const patientToken = localStorage.getItem('patient_token');

  // 需要管理员认证
  if (to.meta.requiresAdmin && !adminToken) {
    next('/admin/login');
    return;
  }

  // 需要患者认证
  if (to.meta.requiresPatient && !patientToken) {
    next('/user/login');
    return;
  }

  // 已登录管理员访问管理员登录页
  if (to.path === '/admin/login' && adminToken) {
    next('/admin/home');
    return;
  }

  // 已登录患者访问患者登录页
  if (to.path === '/user/login' && patientToken) {
    next('/user/home');
    return;
  }

  // 需要医生认证
  if (to.meta.requiresDoctor && !localStorage.getItem('doctor_token')) {
    next('/doctor/login');
    return;
  }

  // 已登录医生访问登录页
  if (to.path === '/doctor/login' && localStorage.getItem('doctor_token')) {
    next('/doctor/home');
    return;
  }

  next();
});

export default router;