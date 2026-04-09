package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smart_medical_server.DTO.DiagnosisCreateDTO;
import com.medical.smart_medical_server.DTO.DiagnosisUpdateDTO;
import com.medical.smart_medical_server.VO.DiagnosisVO;
import com.medical.smart_medical_server.entity.*;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.*;
import com.medical.smart_medical_server.service.DiagnosisService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiagnosisServiceImpl implements DiagnosisService {

    @Autowired
    private DiagnosisMapper diagnosisMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DoctorScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public DiagnosisVO create(DiagnosisCreateDTO dto, Long doctorId) {
        // 验证挂号单
        Appointment appointment = appointmentMapper.selectById(dto.getAppointmentId());
        if (appointment == null) {
            throw new BusinessException("挂号单不存在");
        }

        // 验证是否是该医生的患者
        DoctorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
        if (schedule == null || !schedule.getDoctorId().equals(doctorId)) {
            throw new BusinessException("无权操作该挂号单");
        }

        // 检查是否已有诊疗记录
        LambdaQueryWrapper<Diagnosis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Diagnosis::getAppointmentId, dto.getAppointmentId());
        if (diagnosisMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该挂号单已有诊疗记录");
        }

        // 创建诊疗记录
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setAppointmentId(dto.getAppointmentId());
        diagnosis.setPatientId(appointment.getPatientId());
        diagnosis.setDoctorId(doctorId);
        diagnosis.setSymptom(dto.getSymptom());
        diagnosis.setDiagnosisResult(dto.getDiagnosisResult());
        diagnosis.setPrescription(dto.getPrescription());

        diagnosisMapper.insert(diagnosis);

        // 更新挂号单状态为已完成
        appointment.setStatus(1);
        appointmentMapper.updateById(appointment);

        return convertToVO(diagnosis);
    }

    @Override
    public DiagnosisVO update(Long id, DiagnosisUpdateDTO dto, Long doctorId) {
        Diagnosis diagnosis = diagnosisMapper.selectById(id);
        if (diagnosis == null) {
            throw new BusinessException("诊疗记录不存在");
        }

        if (!diagnosis.getDoctorId().equals(doctorId)) {
            throw new BusinessException("无权修改该诊疗记录");
        }

        if (dto.getSymptom() != null) {
            diagnosis.setSymptom(dto.getSymptom());
        }
        if (dto.getDiagnosisResult() != null) {
            diagnosis.setDiagnosisResult(dto.getDiagnosisResult());
        }
        if (dto.getPrescription() != null) {
            diagnosis.setPrescription(dto.getPrescription());
        }

        diagnosisMapper.updateById(diagnosis);

        return convertToVO(diagnosis);
    }

    @Override
    public DiagnosisVO getById(Long id) {
        Diagnosis diagnosis = diagnosisMapper.selectById(id);
        if (diagnosis == null) {
            return null;
        }
        return convertToVO(diagnosis);
    }

    @Override
    public DiagnosisVO getByAppointmentId(Long appointmentId) {
        LambdaQueryWrapper<Diagnosis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Diagnosis::getAppointmentId, appointmentId);
        Diagnosis diagnosis = diagnosisMapper.selectOne(wrapper);
        if (diagnosis == null) {
            return null;
        }
        return convertToVO(diagnosis);
    }

    @Override
    public Page<DiagnosisVO> getDoctorDiagnosisList(Long doctorId, Integer pageNum, Integer pageSize) {
        Page<Diagnosis> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Diagnosis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Diagnosis::getDoctorId, doctorId);
        wrapper.orderByDesc(Diagnosis::getCreateTime);

        Page<Diagnosis> result = diagnosisMapper.selectPage(page, wrapper);

        Page<DiagnosisVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    @Override
    public Page<DiagnosisVO> getPatientDiagnosisList(Long patientId, Integer pageNum, Integer pageSize) {
        Page<Diagnosis> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Diagnosis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Diagnosis::getPatientId, patientId);
        wrapper.orderByDesc(Diagnosis::getCreateTime);

        Page<Diagnosis> result = diagnosisMapper.selectPage(page, wrapper);

        Page<DiagnosisVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::convertToVO).toList());

        return voPage;
    }

    private DiagnosisVO convertToVO(Diagnosis diagnosis) {
        DiagnosisVO vo = new DiagnosisVO();
        BeanUtils.copyProperties(diagnosis, vo);

        // 获取患者信息
        Patient patient = patientMapper.selectById(diagnosis.getPatientId());
        if (patient != null) {
            vo.setPatientName(patient.getName());
        }

        // 获取医生信息
        Doctor doctor = doctorMapper.selectById(diagnosis.getDoctorId());
        if (doctor != null) {
            vo.setDoctorName(doctor.getName());
        }

        // 获取挂号信息
        Appointment appointment = appointmentMapper.selectById(diagnosis.getAppointmentId());
        if (appointment != null) {
            DoctorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
            if (schedule != null) {
                vo.setWorkDate(schedule.getWorkDate().toString());
                vo.setShiftType(schedule.getShiftType());
            }
        }

        return vo;
    }
}