package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.ScheduleCreateDTO;
import com.medical.smart_medical_server.DTO.ScheduleQueryDTO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.DoctorScheduleService;
import com.medical.smart_medical_server.VO.DoctorScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生排班控制器
 */
@RestController
@RequestMapping("/api/schedule")
public class DoctorScheduleController {

    @Autowired
    private DoctorScheduleService scheduleService;

    @GetMapping("/list")
    public Result<IPage<DoctorScheduleVO>> list(ScheduleQueryDTO queryDTO) {
        IPage<DoctorScheduleVO> page = scheduleService.querySchedulePage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<DoctorScheduleVO> getById(@PathVariable Long id) {
        DoctorScheduleVO scheduleVO = scheduleService.getScheduleById(id);
        return Result.success(scheduleVO);
    }

    @PostMapping
    public Result<DoctorScheduleVO> create(@Validated @RequestBody ScheduleCreateDTO createDTO) {
        DoctorScheduleVO scheduleVO = scheduleService.createSchedule(createDTO);
        return Result.success(scheduleVO);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        scheduleService.updateScheduleStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return Result.success();
    }
}