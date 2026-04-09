package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.entity.DoctorSchedule;
import com.medical.smart_medical_server.service.DoctorService;
import com.medical.smart_medical_server.service.DoctorScheduleService;
import com.medical.smart_medical_server.service.DepartmentService;
import com.medical.smart_medical_server.VO.SchedulePublicVO;
import com.medical.smart_medical_server.VO.DepartmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 排班公开接口（用户端使用，无需认证）
 */
@RestController
@RequestMapping("/api/public/schedule")
public class PublicScheduleController {

    @Autowired
    private DoctorScheduleService scheduleService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 查询可挂号的排班列表
     */
    @GetMapping("/available")
    public Result<IPage<SchedulePublicVO>> getAvailableSchedules(
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();

        // 只查询正常状态且有剩余号源的排班
        wrapper.eq(DoctorSchedule::getStatus, 1);
        wrapper.gt(DoctorSchedule::getRemainingQuota, 0);

        // 只查询今天及以后的排班
        wrapper.ge(DoctorSchedule::getWorkDate, LocalDate.now());

        if (deptId != null) {
            wrapper.eq(DoctorSchedule::getDeptId, deptId);
        }
        if (doctorId != null) {
            wrapper.eq(DoctorSchedule::getDoctorId, doctorId);
        }
        if (startDate != null) {
            wrapper.ge(DoctorSchedule::getWorkDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(DoctorSchedule::getWorkDate, endDate);
        }

        wrapper.orderByAsc(DoctorSchedule::getWorkDate)
                .orderByAsc(DoctorSchedule::getShiftType);

        Page<DoctorSchedule> page = new Page<>(pageNum, pageSize);
        IPage<DoctorSchedule> schedulePage = scheduleService.page(page, wrapper);

        // 转换为公开 VO
        IPage<SchedulePublicVO> resultPage = schedulePage.convert(schedule -> {
            SchedulePublicVO vo = new SchedulePublicVO();
            BeanUtils.copyProperties(schedule, vo);

            // 关联查询医生信息
            try {
                Doctor doctor = doctorService.getById(schedule.getDoctorId());
                if (doctor != null) {
                    vo.setDoctorName(doctor.getName());
                    vo.setDoctorJobTitle(doctor.getJobTitle());
                }
            } catch (Exception e) {
                vo.setDoctorName("未知医生");
            }

            // 关联查询科室名称
            try {
                DepartmentVO dept = departmentService.getDepartmentById(schedule.getDeptId());
                vo.setDeptName(dept.getDeptName());
            } catch (Exception e) {
                vo.setDeptName("未知科室");
            }

            return vo;
        });

        return Result.success(resultPage);
    }

    /**
     * 根据医生ID查询可挂号排班
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<IPage<SchedulePublicVO>> getByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return getAvailableSchedules(null, doctorId, null, null, pageNum, pageSize);
    }

    /**
     * 根据科室ID查询可挂号排班
     */
    @GetMapping("/department/{deptId}")
    public Result<IPage<SchedulePublicVO>> getByDepartment(
            @PathVariable Long deptId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return getAvailableSchedules(deptId, null, null, null, pageNum, pageSize);
    }

    /**
     * 获取排班详情
     */
    @GetMapping("/{id}")
    public Result<SchedulePublicVO> getById(@PathVariable Long id) {
        DoctorSchedule schedule = scheduleService.getById(id);
        if (schedule == null) {
            return Result.error("排班不存在");
        }

        SchedulePublicVO vo = new SchedulePublicVO();
        BeanUtils.copyProperties(schedule, vo);

        // 关联查询医生信息
        try {
            Doctor doctor = doctorService.getById(schedule.getDoctorId());
            if (doctor != null) {
                vo.setDoctorName(doctor.getName());
                vo.setDoctorJobTitle(doctor.getJobTitle());
            }
        } catch (Exception e) {
            vo.setDoctorName("未知医生");
        }

        // 关联查询科室名称
        try {
            DepartmentVO dept = departmentService.getDepartmentById(schedule.getDeptId());
            vo.setDeptName(dept.getDeptName());
        } catch (Exception e) {
            vo.setDeptName("未知科室");
        }

        return Result.success(vo);
    }
}