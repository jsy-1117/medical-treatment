import request from '@/utils/request';
import type { Result } from '@/types/admin';
import type { PatientVO, PatientUpdateDTO } from '@/types/patient';


/**
 * 患者认证 API
 */
export const patientAuthApi = {
    // 患者登录
    login(data: { username: string; password: string }): Promise<Result<any>> {
        return request.post('/api/patient/login', data);
    },

    // 患者注册
    register(data: { username: string; password: string; name: string; phone?: string }): Promise<Result<any>> {
        return request.post('/api/patient/register', data);
    },

    // 获取当前用户信息
    getProfile(): Promise<Result<PatientVO>> {
        return request.get('/api/patient/profile');
    },

    // 更新用户信息
    updateProfile(data: PatientUpdateDTO): Promise<Result<PatientVO>> {
        return request.put('/api/patient/profile', data);
    },

    // 修改密码
    updatePassword(data: { oldPassword: string; newPassword: string }): Promise<Result<void>> {
        return request.put('/api/patient/password', data);
    }
};