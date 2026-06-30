import request from '@/utils/request';
import type { Result } from '@/types/admin';
import type { PageResult } from '@/types/patient';
import type {
    DoctorInfo,
    DoctorScheduleVO,
    TodayPatient,
    DiagnosisVO,
    DiagnosisCreateDTO,
    DiagnosisUpdateDTO,
    DiagnosisSuggestionRequest
} from '@/types/doctor-portal';

/**
 * 医生认证 API
 */
export const doctorAuthApi = {
    // 医生登录
    login(data: { username: string; password: string }): Promise<Result<any>> {
        return request.post('/api/doctor-auth/login', data);
    },

    // 获取医生信息
    getProfile(): Promise<Result<DoctorInfo>> {
        return request.get('/api/doctor-auth/profile');
    }
};

/**
 * 医生端功能 API
 */
export const doctorPortalApi = {
    // ========== 排班管理 ==========

    // 获取我的排班列表
    getScheduleList(params?: {
        pageNum?: number;
        pageSize?: number;
        startDate?: string;
        endDate?: string;
    }): Promise<Result<PageResult<DoctorScheduleVO>>> {
        return request.get('/api/doctor-portal/schedule/list', { params });
    },

    // 修改排班状态
    updateScheduleStatus(id: number, status: number): Promise<Result<void>> {
        return request.put(`/api/doctor-portal/schedule/${id}/status`, null, { params: { status } });
    },

    // ========== 患者管理 ==========

    // 获取今日待诊患者
    getTodayPatients(): Promise<Result<TodayPatient[]>> {
        return request.get('/api/doctor-portal/patient/today');
    },

    // 获取历史患者
    getHistoryPatients(params?: {
        pageNum?: number;
        pageSize?: number;
        patientName?: string;
    }): Promise<Result<PageResult<any>>> {
        return request.get('/api/doctor-portal/patient/history', { params });
    },

    // ========== 诊疗管理 ==========

    // 创建诊疗记录
    createDiagnosis(data: DiagnosisCreateDTO): Promise<Result<DiagnosisVO>> {
        return request.post('/api/doctor-portal/diagnosis', data);
    },

    // 更新诊疗记录
    updateDiagnosis(id: number, data: DiagnosisUpdateDTO): Promise<Result<DiagnosisVO>> {
        return request.put(`/api/doctor-portal/diagnosis/${id}`, data);
    },

    // 获取诊疗详情
    getDiagnosisById(id: number): Promise<Result<DiagnosisVO>> {
        return request.get(`/api/doctor-portal/diagnosis/${id}`);
    },

    // 根据挂号单获取诊疗记录
    getDiagnosisByAppointmentId(appointmentId: number): Promise<Result<DiagnosisVO>> {
        return request.get(`/api/doctor-portal/diagnosis/appointment/${appointmentId}`);
    },

    // 获取诊疗记录列表
    getDiagnosisList(params?: {
        pageNum?: number;
        pageSize?: number;
    }): Promise<Result<PageResult<DiagnosisVO>>> {
        return request.get('/api/doctor-portal/diagnosis/list', { params });
    },

    // 修改密码
    updatePassword(data: { oldPassword: string; newPassword: string }): Promise<Result<void>> {
        return request.put('/api/doctor-auth/password', data);
    },

    // AI 诊断建议
    getDiagnosisSuggestion(data: DiagnosisSuggestionRequest): Promise<Result<string>> {
        return request.post('/api/doctor-portal/diagnosis/suggest', data);
    }
};