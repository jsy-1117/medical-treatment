import request from '@/utils/request';
import type { Result } from '@/types/admin';
import type { PageResult } from '@/types/patient';
import type {
    AppointmentVO,
    AppointmentCreateDTO,
    AppointmentQueryDTO
} from '@/types/appointment';

/**
 * 挂号管理 API
 */
export const appointmentApi = {
    /**
     * 分页查询挂号列表（管理员）
     */
    getList(params: AppointmentQueryDTO): Promise<Result<PageResult<AppointmentVO>>> {
        return request.get('/api/appointment/list', { params });
    },

    /**
     * 根据ID获取挂号详情
     */
    getById(id: number): Promise<Result<AppointmentVO>> {
        return request.get(`/api/appointment/${id}`);
    },

    /**
     * 创建挂号（用户端）
     */
    create(data: AppointmentCreateDTO): Promise<Result<AppointmentVO>> {
        return request.post('/api/appointment', data);
    },

    /**
     * 更新挂号状态（管理员）
     */
    updateStatus(id: number, status: number): Promise<Result<void>> {
        return request.put(`/api/appointment/${id}/status`, null, { params: { status } });
    },

    /**
     * 取消挂号（用户端）
     */
    cancel(id: number, patientId: number): Promise<Result<void>> {
        return request.put(`/api/appointment/${id}/cancel`, null, { params: { patientId } });
    },

    /**
     * 删除挂号记录（管理员）
     */
    delete(id: number): Promise<Result<void>> {
        return request.delete(`/api/appointment/${id}`);
    },

    /**
     * 查询患者的挂号记录（用户端）
     */
    getPatientAppointments(patientId: number, pageNum?: number, pageSize?: number): Promise<Result<PageResult<AppointmentVO>>> {
        return request.get(`/api/appointment/patient/${patientId}`, {
            params: { pageNum, pageSize }
        });
    }
};