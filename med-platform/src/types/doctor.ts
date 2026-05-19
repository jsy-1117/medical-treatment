/**
 * 医生类型定义
 */

// 医生 VO
export interface DoctorVO {
    id: number;
    deptId: number;
    deptName?: string;
    username: string;
    name: string;
    jobTitle: string;
    introduction: string;
    phone: string;
    createTime: string;
}

// 医生创建 DTO
export interface DoctorCreateDTO {
    deptId: number;
    username: string;
    password: string;
    name: string;
    jobTitle?: string;
    introduction?: string;
    phone?: string;
}

// 医生更新 DTO
export interface DoctorUpdateDTO {
    deptId?: number;
    name?: string;
    password?: string;
    jobTitle?: string;
    introduction?: string;
    phone?: string;
}

// 医生查询 DTO
export interface DoctorQueryDTO {
    name?: string;
    deptId?: number;
    jobTitle?: string;
    pageNum?: number;
    pageSize?: number;
}

// 排班 VO
export interface DoctorScheduleVO {
    id: number;
    doctorId: number;
    doctorName?: string;
    deptId: number;
    deptName?: string;
    workDate: string;
    shiftType: number;
    shiftTypeDesc?: string;
    quota: number;
    remainingQuota: number;
    status: number;
    statusDesc?: string;
    createTime: string;
}

// 排班创建 DTO
export interface ScheduleCreateDTO {
    doctorId: number;
    deptId: number;
    workDate: string;
    shiftType: number;
    quota: number;
}

// 排班查询 DTO
export interface ScheduleQueryDTO {
    doctorId?: number;
    deptId?: number;
    startDate?: string;
    endDate?: string;
    pageNum?: number;
    pageSize?: number;
    sortField?: string;
    sortDirection?: string;
}