import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';

const request: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
});

// 请求拦截器
request.interceptors.request.use(
    (config: any) => {
        let token: string | null = null;

        // 根据当前页面路由判断使用哪个 token
        const currentPath = window.location.pathname;

        if (currentPath.startsWith('/admin')) {
            // 管理员后台的所有请求都使用 admin_token
            token = localStorage.getItem('admin_token');
        } else if (currentPath.startsWith('/doctor')) {
            // 医生端
            token = localStorage.getItem('doctor_token');
        } else if (currentPath.startsWith('/user') || currentPath.startsWith('/portal')) {
            // 患者端
            token = localStorage.getItem('patient_token');
        } else {
            // 默认尝试获取任意一个可用的 token
            token = localStorage.getItem('admin_token')
                || localStorage.getItem('doctor_token')
                || localStorage.getItem('patient_token');
        }

        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data;

        // 如果状态码不是 200，则判断为错误
        if (res.code !== 200) {
            ElMessage.error(res.msg || '请求失败');

            // 401: Token 过期或无效
            if (res.code === 401) {
                localStorage.removeItem('admin_token');
                localStorage.removeItem('admin_info');
                router.push('/login');
            }

            return Promise.reject(new Error(res.msg || '请求失败'));
        }

        return res;
    },
    (error) => {
        console.error('请求错误：', error);
        ElMessage.error(error.message || '网络错误，请稍后重试');
        return Promise.reject(error);
    }
);

export default request;