import { defineStore } from 'pinia';
import { ref } from 'vue';
import { patientAuthApi } from '@/api/patientAuth';
import type { PatientLoginDTO, PatientVO, PatientUpdateDTO } from '@/types/patient';
import { ElMessage } from 'element-plus';
import router from '@/router';

export const usePatientStore = defineStore('patient', () => {
    // 状态
    const token = ref<string>(localStorage.getItem('patient_token') || '');
    const patientInfo = ref<PatientVO | null>(
        JSON.parse(localStorage.getItem('patient_info') || 'null')
    );

    /**
     * 患者登录
     */
    const login = async (loginData: PatientLoginDTO) => {
        try {
            const res = await patientAuthApi.login(loginData);

            // 保存 Token
            token.value = res.data.token;
            localStorage.setItem('patient_token', res.data.token);

            // ✅ 登录成功后，立即获取完整的用户信息
            const profileRes = await patientAuthApi.getProfile();
            patientInfo.value = profileRes.data;
            localStorage.setItem('patient_info', JSON.stringify(profileRes.data));

            ElMessage.success('登录成功');
            router.push('/');
        } catch (error) {
            console.error('登录失败：', error);
            throw error;
        }
    };

    /**
     * 更新用户信息
     */
    const updateProfile = async (data: PatientUpdateDTO) => {
        try {
            const res = await patientAuthApi.updateProfile(data);
            if (res.code === 200) {
                // 更新本地存储的用户信息
                patientInfo.value = { ...patientInfo.value, ...res.data } as PatientVO;
                localStorage.setItem('patient_info', JSON.stringify(patientInfo.value));
                ElMessage.success('信息更新成功');
                return true;
            }
            return false;
        } catch (error) {
            console.error('更新失败：', error);
            throw error;
        }
    };

    /**
     * 检查用户信息是否完整
     */
    const isProfileComplete = () => {
        if (!patientInfo.value) return false;
        return !!(
            patientInfo.value.name &&
            patientInfo.value.phone &&
            patientInfo.value.gender &&
            patientInfo.value.age
        );
    };

    /**
     * 登出
     */
    const logout = () => {
        token.value = '';
        patientInfo.value = null;
        localStorage.removeItem('patient_token');
        localStorage.removeItem('patient_info');
        router.push('/');
        ElMessage.success('退出登录成功');
    };

    return {
        token,
        patientInfo,
        login,
        updateProfile,
        isProfileComplete,
        logout
    };
});