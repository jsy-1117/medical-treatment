package com.medical.smart_medical_server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smart_medical_server.DTO.DiagnosisCreateDTO;
import com.medical.smart_medical_server.DTO.DiagnosisUpdateDTO;
import com.medical.smart_medical_server.VO.DiagnosisVO;

public interface DiagnosisService {

    /**
     * 创建诊疗记录
     */
    DiagnosisVO create(DiagnosisCreateDTO dto, Long doctorId);

    /**
     * 更新诊疗记录
     */
    DiagnosisVO update(Long id, DiagnosisUpdateDTO dto, Long doctorId);

    /**
     * 根据ID获取诊疗记录
     */
    DiagnosisVO getById(Long id);

    /**
     * 根据挂号单ID获取诊疗记录
     */
    DiagnosisVO getByAppointmentId(Long appointmentId);

    /**
     * 获取医生的诊疗记录列表
     */
    Page<DiagnosisVO> getDoctorDiagnosisList(Long doctorId, Integer pageNum, Integer pageSize);

    /**
     * 获取患者的诊疗记录列表
     */
    Page<DiagnosisVO> getPatientDiagnosisList(Long patientId, Integer pageNum, Integer pageSize);
}