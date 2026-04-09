/**
 * 公开接口类型定义
 */

// 医生公开信息 VO
export interface DoctorPublicVO {
    id: number;
    deptId: number;
    deptName?: string;
    name: string;
    jobTitle: string;
    introduction: string;
}

// 排班公开信息 VO
export interface SchedulePublicVO {
    id: number;
    doctorId: number;
    doctorName?: string;
    doctorJobTitle?: string;
    deptId: number;
    deptName?: string;
    workDate: string;
    shiftType: number;
    shiftTypeDesc?: string;
    remainingQuota: number;
    status: number;
    canBook?: boolean;
}