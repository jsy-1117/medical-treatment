import request from '@/utils/request';
import type { Result } from '@/types/admin';
import type {
    PatientVO,
    PatientCreateDTO,
    PatientUpdateDTO,
    PatientQueryDTO,
    PageResult
} from '@/types/patient';

/**
 * 患者管理 API
 */
export const patientApi = {
    /**
     * 分页查询患者列表
     */
    getList(params: PatientQueryDTO): Promise<Result<PageResult<PatientVO>>> {
        return request.get('/api/patient/list', { params });
    },

    /**
     * 根据ID获取患者详情
     */
    getById(id: number): Promise<Result<PatientVO>> {
        return request.get(`/api/patient/${id}`);
    },

    /**
     * 创建患者
     */
    create(data: PatientCreateDTO): Promise<Result<PatientVO>> {
        return request.post('/api/patient', data);
    },

    /**
     * 更新患者信息
     */
    update(id: number, data: PatientUpdateDTO): Promise<Result<PatientVO>> {
        return request.put(`/api/patient/${id}`, data);
    },

    /**
     * 删除患者
     */
    delete(id: number): Promise<Result<void>> {
        return request.delete(`/api/patient/${id}`);
    }
};