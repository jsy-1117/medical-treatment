import { defineStore } from 'pinia';
import { ref } from 'vue';
import { doctorAuthApi } from '@/api/doctorPortal';
import type { DoctorLoginDTO, DoctorInfo } from '@/types/doctor-portal';
import { ElMessage } from 'element-plus';
import router from '@/router';

export const useDoctorStore = defineStore('doctor', () => {
    // 状态
    const token = ref<string>(localStorage.getItem('doctor_token') || '');
    const doctorInfo = ref<DoctorInfo | null>(
        JSON.parse(localStorage.getItem('doctor_info') || 'null')
    );

    /**
     * 医生登录
     */
    const login = async (loginData: DoctorLoginDTO) => {
        try {
            const res = await doctorAuthApi.login(loginData);

            // 保存 Token 和医生信息
            token.value = res.data.token;
            doctorInfo.value = res.data;

            // 持久化到 localStorage
            localStorage.setItem('doctor_token', res.data.token);
            localStorage.setItem('doctor_info', JSON.stringify(res.data));

            ElMessage.success('登录成功');
            router.push('/doctor/home');
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
        doctorInfo.value = null;
        localStorage.removeItem('doctor_token');
        localStorage.removeItem('doctor_info');
        router.push('/doctor/login');
        ElMessage.success('退出登录成功');
    };

    return {
        token,
        doctorInfo,
        login,
        logout
    };
});