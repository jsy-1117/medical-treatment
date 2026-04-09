package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.DTO.DiagnosisCreateDTO;
import com.medical.smart_medical_server.DTO.DiagnosisUpdateDTO;
import com.medical.smart_medical_server.VO.DiagnosisVO;
import com.medical.smart_medical_server.entity.*;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.*;
import com.medical.smart_medical_server.service.DiagnosisService;
import com.medical.smart_medical_server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * 医生端接口控制器
 */
@RestController
@RequestMapping("/api/doctor-portal")
public class DoctorPortalController {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DoctorScheduleMapper scheduleMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DiagnosisService diagnosisService;

    @Autowired
    private JwtUtil jwtUtil;

    // ========== 排班管理 ==========

    /**
     * 获取我的排班列表
     */
    @GetMapping("/schedule/list")
    public Result<Page<Map<String, Object>>> getMyScheduleList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Long doctorId = getDoctorIdFromToken(authHeader);

        Page<DoctorSchedule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorSchedule::getDoctorId, doctorId);

        if (startDate != null) {
            wrapper.ge(DoctorSchedule::getWorkDate, LocalDate.parse(startDate));
        }
        if (endDate != null) {
            wrapper.le(DoctorSchedule::getWorkDate, LocalDate.parse(endDate));
        }

        wrapper.orderByAsc(DoctorSchedule::getWorkDate);
        wrapper.orderByAsc(DoctorSchedule::getShiftType);

        Page<DoctorSchedule> result = scheduleMapper.selectPage(page, wrapper);

        // 转换为带有额外信息的 Map
        List<Map<String, Object>> records = new ArrayList<>();
        for (DoctorSchedule schedule : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", schedule.getId());
            map.put("workDate", schedule.getWorkDate().toString());
            map.put("shiftType", schedule.getShiftType());
            map.put("shiftTypeDesc", schedule.getShiftType() == 1 ? "上午" : "下午");
            map.put("quota", schedule.getQuota());
            map.put("remainingQuota", schedule.getRemainingQuota());
            map.put("status", schedule.getStatus());
            map.put("statusDesc", schedule.getStatus() == 1 ? "正常" : "停诊");
            records.add(map);
        }

        Page<Map<String, Object>> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(records);

        return Result.success(voPage);
    }

    /**
     * 修改排班状态（停诊/恢复）
     */
    @PutMapping("/schedule/{id}/status")
    public Result<Void> updateScheduleStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestParam Integer status) {

        Long doctorId = getDoctorIdFromToken(authHeader);

        DoctorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }

        if (!schedule.getDoctorId().equals(doctorId)) {
            throw new BusinessException("无权修改该排班");
        }

        schedule.setStatus(status);
        scheduleMapper.updateById(schedule);

        return Result.success(null);
    }

    // ========== 患者管理 ==========

    /**
     * 获取今日待诊患者列表
     */
    @GetMapping("/patient/today")
    public Result<List<Map<String, Object>>> getTodayPatients(
            @RequestHeader("Authorization") String authHeader) {

        Long doctorId = getDoctorIdFromToken(authHeader);

        // 获取今日的排班
        LambdaQueryWrapper<DoctorSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(DoctorSchedule::getDoctorId, doctorId);
        scheduleWrapper.eq(DoctorSchedule::getWorkDate, LocalDate.now());
        scheduleWrapper.eq(DoctorSchedule::getStatus, 1);
        List<DoctorSchedule> schedules = scheduleMapper.selectList(scheduleWrapper);

        if (schedules.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 获取这些排班的挂号单
        List<Long> scheduleIds = schedules.stream().map(DoctorSchedule::getId).toList();
        LambdaQueryWrapper<Appointment> appointmentWrapper = new LambdaQueryWrapper<>();
        appointmentWrapper.in(Appointment::getScheduleId, scheduleIds);
        appointmentWrapper.eq(Appointment::getStatus, 0); // 待就诊
        appointmentWrapper.orderByAsc(Appointment::getCreateTime);
        List<Appointment> appointments = appointmentMapper.selectList(appointmentWrapper);

        // 转换为返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            Patient patient = patientMapper.selectById(appointment.getPatientId());
            DoctorSchedule schedule = schedules.stream()
                    .filter(s -> s.getId().equals(appointment.getScheduleId()))
                    .findFirst().orElse(null);

            Map<String, Object> map = new HashMap<>();
            map.put("appointmentId", appointment.getId());
            map.put("patientId", patient.getId());
            map.put("patientName", patient.getName());
            map.put("patientPhone", patient.getPhone());
            map.put("patientGender", patient.getGender());
            map.put("patientAge", patient.getAge());
            map.put("shiftType", schedule != null ? schedule.getShiftType() : null);
            map.put("createTime", appointment.getCreateTime());

            // 检查是否已有诊疗记录
            DiagnosisVO diagnosis = diagnosisService.getByAppointmentId(appointment.getId());
            map.put("hasDiagnosis", diagnosis != null);

            result.add(map);
        }

        return Result.success(result);
    }

    /**
     * 获取历史患者列表
     */
    @GetMapping("/patient/history")
    public Result<Page<Map<String, Object>>> getHistoryPatients(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String patientName) {

        Long doctorId = getDoctorIdFromToken(authHeader);

        // 获取该医生的所有诊疗记录
        Page<DiagnosisVO> diagnosisPage = diagnosisService.getDoctorDiagnosisList(doctorId, pageNum, pageSize);

        List<Map<String, Object>> records = new ArrayList<>();
        for (DiagnosisVO diagnosis : diagnosisPage.getRecords()) {
            // 如果指定了患者名称，进行过滤
            if (patientName != null && !diagnosis.getPatientName().contains(patientName)) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("diagnosisId", diagnosis.getId());
            map.put("appointmentId", diagnosis.getAppointmentId());
            map.put("patientId", diagnosis.getPatientId());
            map.put("patientName", diagnosis.getPatientName());
            map.put("workDate", diagnosis.getWorkDate());
            map.put("shiftType", diagnosis.getShiftType());
            map.put("diagnosisResult", diagnosis.getDiagnosisResult());
            map.put("createTime", diagnosis.getCreateTime());
            records.add(map);
        }

        Page<Map<String, Object>> result = new Page<>(diagnosisPage.getCurrent(), diagnosisPage.getSize(), diagnosisPage.getTotal());
        result.setRecords(records);

        return Result.success(result);
    }

    // ========== 诊疗管理 ==========

    /**
     * 创建诊疗记录
     */
    @PostMapping("/diagnosis")
    public Result<DiagnosisVO> createDiagnosis(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DiagnosisCreateDTO dto) {

        Long doctorId = getDoctorIdFromToken(authHeader);
        DiagnosisVO vo = diagnosisService.create(dto, doctorId);
        return Result.success(vo);
    }

    /**
     * 更新诊疗记录
     */
    @PutMapping("/diagnosis/{id}")
    public Result<DiagnosisVO> updateDiagnosis(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody DiagnosisUpdateDTO dto) {

        Long doctorId = getDoctorIdFromToken(authHeader);
        DiagnosisVO vo = diagnosisService.update(id, dto, doctorId);
        return Result.success(vo);
    }

    /**
     * 获取诊疗详情
     */
    @GetMapping("/diagnosis/{id}")
    public Result<DiagnosisVO> getDiagnosisById(@PathVariable Long id) {
        DiagnosisVO vo = diagnosisService.getById(id);
        return Result.success(vo);
    }

    /**
     * 根据挂号单获取诊疗记录
     */
    @GetMapping("/diagnosis/appointment/{appointmentId}")
    public Result<DiagnosisVO> getDiagnosisByAppointmentId(@PathVariable Long appointmentId) {
        DiagnosisVO vo = diagnosisService.getByAppointmentId(appointmentId);
        return Result.success(vo);
    }

    /**
     * 获取我的诊疗记录列表
     */
    @GetMapping("/diagnosis/list")
    public Result<Page<DiagnosisVO>> getMyDiagnosisList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Long doctorId = getDoctorIdFromToken(authHeader);
        Page<DiagnosisVO> page = diagnosisService.getDoctorDiagnosisList(doctorId, pageNum, pageSize);
        return Result.success(page);
    }

    // ========== 工具方法 ==========

    private Long getDoctorIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new BusinessException("无效的Token");
    }
}