import request from '@/utils/request';
import type {
    Result,
    AdminLoginDTO,
    AdminRegisterDTO,
    AdminUpdateDTO,
    AdminVO,
    AdminLoginVO
} from '@/types/admin';

/**
 * 管理员 API
 */
export const adminApi = {
    /**
     * 管理员登录
     */
    login(data: AdminLoginDTO): Promise<Result<AdminLoginVO>> {
        return request.post('/api/admin/login', data);
    },

    /**
     * 管理员注册
     */
    register(data: AdminRegisterDTO): Promise<Result<AdminVO>> {
        return request.post('/api/admin/register', data);
    },

    /**
     * 获取当前管理员信息
     */
    getCurrentAdmin(): Promise<Result<AdminVO>> {
        return request.get('/api/admin/current');
    },

    /**
     * 更新管理员信息
     */
    updateAdmin(id: number, data: AdminUpdateDTO): Promise<Result<AdminVO>> {
        return request.put(`/api/admin/${id}`, data);
    },

    /**
     * 删除管理员
     */
    deleteAdmin(id: number): Promise<Result<void>> {
        return request.delete(`/api/admin/${id}`);
    },

    // 修改密码
    updatePassword(data: { oldPassword: string; newPassword: string }): Promise<Result<void>> {
        return request.put('/api/admin/password', data);
    }
};