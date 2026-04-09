import request from '@/utils/request';
import type { Result } from '@/types/admin';
import type { PageResult } from '@/types/patient';
import type {
    DepartmentVO,
    DepartmentCreateDTO,
    DepartmentUpdateDTO,
    DepartmentQueryDTO
} from '@/types/department';

/**
 * 科室管理 API
 */
export const departmentApi = {
    /**
     * 分页查询科室列表（管理员）
     */
    getList(params: DepartmentQueryDTO): Promise<Result<PageResult<DepartmentVO>>> {
        return request.get('/api/department/list', { params });
    },

    /**
     * 获取所有科室（用户端，用于下拉选择）
     */
    getAll(): Promise<Result<DepartmentVO[]>> {
        return request.get('/api/department/all');
    },

    /**
     * 根据ID获取科室详情
     */
    getById(id: number): Promise<Result<DepartmentVO>> {
        return request.get(`/api/department/${id}`);
    },

    /**
     * 创建科室（管理员）
     */
    create(data: DepartmentCreateDTO): Promise<Result<DepartmentVO>> {
        return request.post('/api/department', data);
    },

    /**
     * 更新科室（管理员）
     */
    update(id: number, data: DepartmentUpdateDTO): Promise<Result<DepartmentVO>> {
        return request.put(`/api/department/${id}`, data);
    },

    /**
     * 删除科室（管理员）
     */
    delete(id: number): Promise<Result<void>> {
        return request.delete(`/api/department/${id}`);
    }
};