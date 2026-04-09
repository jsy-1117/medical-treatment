package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.AppointmentCreateDTO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.AppointmentService;
import com.medical.smart_medical_server.VO.AppointmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 患者挂号接口（患者端使用）
 */
@RestController
@RequestMapping("/api/patient/appointment")
public class PatientAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 创建挂号
     */
    @PostMapping
    public Result<AppointmentVO> create(@Validated @RequestBody AppointmentCreateDTO createDTO) {
        AppointmentVO appointmentVO = appointmentService.createAppointment(createDTO);
        return Result.success(appointmentVO);
    }

    /**
     * 查询我的挂号记录
     */
    @GetMapping("/my/{patientId}")
    public Result<IPage<AppointmentVO>> myAppointments(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<AppointmentVO> page = appointmentService.queryPatientAppointments(patientId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 取消挂号
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam Long patientId) {
        appointmentService.cancelAppointment(id, patientId);
        return Result.success();
    }

    /**
     * 获取挂号详情
     */
    @GetMapping("/{id}")
    public Result<AppointmentVO> getById(@PathVariable Long id) {
        AppointmentVO appointmentVO = appointmentService.getAppointmentById(id);
        return Result.success(appointmentVO);
    }
}