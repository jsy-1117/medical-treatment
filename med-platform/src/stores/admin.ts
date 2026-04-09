import { defineStore } from 'pinia';
import { ref } from 'vue';
import { adminApi } from '@/api/admin';
import type { AdminLoginDTO, AdminVO } from '@/types/admin';
import { ElMessage } from 'element-plus';
import router from '@/router';

export const useAdminStore = defineStore('admin', () => {
    // 状态
    const token = ref<string>(localStorage.getItem('admin_token') || '');
    const adminInfo = ref<AdminVO | null>(
        JSON.parse(localStorage.getItem('admin_info') || 'null')
    );

    /**
     * 登录
     */
    const login = async (loginData: AdminLoginDTO) => {
        try {
            const res = await adminApi.login(loginData);

            // 保存 Token 和用户信息
            token.value = res.data.token;
            adminInfo.value = {
                id: res.data.id,
                username: res.data.username,
                name: res.data.name,
                createTime: ''
            };

            // 持久化到 localStorage
            localStorage.setItem('admin_token', res.data.token);
            localStorage.setItem('admin_info', JSON.stringify(adminInfo.value));

            ElMessage.success('登录成功');
            router.push('/admin/home');
        } catch (error) {
            console.error('登录失败：', error);
            throw error;
        }
    };

    /**
     * 登出
     */
    const logout = () => {
        token.value = '';
        adminInfo.value = null;
        localStorage.removeItem('admin_token');
        localStorage.removeItem('admin_info');
        router.push('/login');
        ElMessage.success('退出登录成功');
    };

    /**
     * 获取当前管理员信息
     */
    const getCurrentAdmin = async () => {
        try {
            const res = await adminApi.getCurrentAdmin();
            adminInfo.value = res.data;
            localStorage.setItem('admin_info', JSON.stringify(res.data));
        } catch (error) {
            console.error('获取管理员信息失败：', error);
        }
    };

    return {
        token,
        adminInfo,
        login,
        logout,
        getCurrentAdmin
    };
});