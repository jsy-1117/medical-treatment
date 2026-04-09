package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.AppointmentCreateDTO;
import com.medical.smart_medical_server.DTO.AppointmentQueryDTO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.AppointmentService;
import com.medical.smart_medical_server.VO.AppointmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 挂号管理控制器（管理员端 + 用户端通用）
 */
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 分页查询挂号记录（管理员使用）
     */
    @GetMapping("/list")
    public Result<IPage<AppointmentVO>> list(AppointmentQueryDTO queryDTO) {
        IPage<AppointmentVO> page = appointmentService.queryAppointmentPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据ID获取挂号详情
     */
    @GetMapping("/{id}")
    public Result<AppointmentVO> getById(@PathVariable Long id) {
        AppointmentVO appointmentVO = appointmentService.getAppointmentById(id);
        return Result.success(appointmentVO);
    }

    /**
     * 创建挂号（用户端挂号）
     */
    @PostMapping
    public Result<AppointmentVO> create(@Validated @RequestBody AppointmentCreateDTO createDTO) {
        AppointmentVO appointmentVO = appointmentService.createAppointment(createDTO);
        return Result.success(appointmentVO);
    }

    /**
     * 更新挂号状态（管理员使用）
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        appointmentService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 取消挂号（用户端使用）
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam Long patientId) {
        appointmentService.cancelAppointment(id, patientId);
        return Result.success();
    }

    /**
     * 删除挂号记录（管理员使用）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return Result.success();
    }

    /**
     * 查询患者的挂号记录（用户端使用）
     */
    @GetMapping("/patient/{patientId}")
    public Result<IPage<AppointmentVO>> patientAppointments(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<AppointmentVO> page = appointmentService.queryPatientAppointments(patientId, pageNum, pageSize);
        return Result.success(page);
    }
}