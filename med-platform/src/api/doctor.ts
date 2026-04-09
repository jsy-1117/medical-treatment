import request from '@/utils/request';
import type { Result } from '@/types/admin';
import type { PageResult } from '@/types/patient';
import type {
    DoctorVO,
    DoctorCreateDTO,
    DoctorUpdateDTO,
    DoctorQueryDTO,
    DoctorScheduleVO,
    ScheduleCreateDTO,
    ScheduleQueryDTO
} from '@/types/doctor';

/**
 * 医生管理 API
 */
export const doctorApi = {
    getList(params: DoctorQueryDTO): Promise<Result<PageResult<DoctorVO>>> {
        return request.get('/api/doctor/list', { params });
    },

    getById(id: number): Promise<Result<DoctorVO>> {
        return request.get(`/api/doctor/${id}`);
    },

    create(data: DoctorCreateDTO): Promise<Result<DoctorVO>> {
        return request.post('/api/doctor', data);
    },

    update(id: number, data: DoctorUpdateDTO): Promise<Result<DoctorVO>> {
        return request.put(`/api/doctor/${id}`, data);
    },

    delete(id: number): Promise<Result<void>> {
        return request.delete(`/api/doctor/${id}`);
    }
};

/**
 * 医生排班 API
 */
export const scheduleApi = {
    getList(params: ScheduleQueryDTO): Promise<Result<PageResult<DoctorScheduleVO>>> {
        return request.get('/api/schedule/list', { params });
    },

    getById(id: number): Promise<Result<DoctorScheduleVO>> {
        return request.get(`/api/schedule/${id}`);
    },

    create(data: ScheduleCreateDTO): Promise<Result<DoctorScheduleVO>> {
        return request.post('/api/schedule', data);
    },

    updateStatus(id: number, status: number): Promise<Result<void>> {
        return request.put(`/api/schedule/${id}/status`, null, { params: { status } });
    },

    delete(id: number): Promise<Result<void>> {
        return request.delete(`/api/schedule/${id}`);
    }
};