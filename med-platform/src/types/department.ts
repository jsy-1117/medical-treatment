/**
 * 科室类型定义
 */

// 科室 VO
export interface DepartmentVO {
    id: number;
    deptName: string;
    deptDesc: string;
    location: string;
    createTime: string;
}

// 科室创建 DTO
export interface DepartmentCreateDTO {
    deptName: string;
    deptDesc?: string;
    location?: string;
}

// 科室更新 DTO
export interface DepartmentUpdateDTO {
    deptName?: string;
    deptDesc?: string;
    location?: string;
}

// 科室查询 DTO
export interface DepartmentQueryDTO {
    deptName?: string;
    pageNum?: number;
    pageSize?: number;
}