/**
 * 医生端类型定义
 */

// 医生信息
export interface DoctorInfo {
    id: number;
    username: string;
    name: string;
    deptId: number;
    jobTitle: string;
    introduction?: string;
    phone?: string;
}

// 医生登录 DTO
export interface DoctorLoginDTO {
    username: string;
    password: string;
}

// 排班信息
export interface DoctorScheduleVO {
    id: number;
    workDate: string;
    shiftType: number;
    shiftTypeDesc: string;
    quota: number;
    remainingQuota: number;
    status: number;
    statusDesc: string;
}

// 待诊患者
export interface TodayPatient {
    appointmentId: number;
    patientId: number;
    patientName: string;
    patientPhone: string;
    patientGender: number;
    patientAge: number;
    shiftType: number;
    createTime: string;
    hasDiagnosis: boolean;
}

// 诊疗记录
export interface DiagnosisVO {
    id: number;
    appointmentId: number;
    patientId: number;
    patientName: string;
    doctorId: number;
    doctorName: string;
    symptom: string;
    diagnosisResult: string;
    prescription: string;
    createTime: string;
    workDate: string;
    shiftType: number;
}

// 诊疗创建 DTO
export interface DiagnosisCreateDTO {
    appointmentId: number;
    symptom?: string;
    diagnosisResult?: string;
    prescription?: string;
}

// 诊疗更新 DTO
export interface DiagnosisUpdateDTO {
    symptom?: string;
    diagnosisResult?: string;
    prescription?: string;
}

// AI 诊断建议请求
export interface DiagnosisSuggestionRequest {
    symptom: string;
    patientId: number;
}