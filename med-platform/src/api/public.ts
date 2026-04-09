import request from '@/utils/request';
import type { Result } from '@/types/admin';
import type { PageResult } from '@/types/patient';
import type { DepartmentVO } from '@/types/department';
import type { DoctorPublicVO, SchedulePublicVO } from '@/types/public';

/**
 * 公开 API（无需认证）
 */
export const publicApi = {
    // 获取科室列表
    getDepartments(): Promise<Result<DepartmentVO[]>> {
        return request.get('/api/public/department/list');
    },

    // 获取医生列表
    getDoctors(params?: { name?: string; deptId?: number; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<DoctorPublicVO>>> {
        return request.get('/api/public/doctor/list', { params });
    },

    // 获取医生详情
    getDoctorById(id: number): Promise<Result<DoctorPublicVO>> {
        return request.get(`/api/public/doctor/${id}`);
    },

    // 获取可挂号排班
    getAvailableSchedules(params?: { deptId?: number; doctorId?: number; startDate?: string; endDate?: string; pageNum?: number; pageSize?: number }): Promise<Result<PageResult<SchedulePublicVO>>> {
        return request.get('/api/public/schedule/available', { params });
    }
};