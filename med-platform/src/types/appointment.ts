/**
 * 挂号类型定义
 */

// 挂号 VO
export interface AppointmentVO {
    id: number;
    patientId: number;
    patientName?: string;
    doctorId: number;
    doctorName?: string;
    scheduleId: number;
    workDate?: string;
    shiftType?: number;
    shiftTypeDesc?: string;
    deptName?: string;
    status: number;
    statusDesc?: string;
    createTime: string;
}

// 挂号创建 DTO
export interface AppointmentCreateDTO {
    patientId: number;
    doctorId: number;
    scheduleId: number;
}

// 挂号查询 DTO
export interface AppointmentQueryDTO {
    patientId?: number;
    doctorId?: number;
    status?: number;
    startDate?: string;
    endDate?: string;
    pageNum?: number;
    pageSize?: number;
}