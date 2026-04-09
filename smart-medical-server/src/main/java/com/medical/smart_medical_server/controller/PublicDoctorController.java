package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.service.DoctorService;
import com.medical.smart_medical_server.service.DepartmentService;
import com.medical.smart_medical_server.VO.DoctorPublicVO;
import com.medical.smart_medical_server.VO.DepartmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 医生公开接口（用户端使用，无需认证）
 */
@RestController
@RequestMapping("/api/public/doctor")
public class PublicDoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 分页查询医生列表（公开信息）
     */
    @GetMapping("/list")
    public Result<IPage<DoctorPublicVO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Doctor::getName, name);
        }
        if (deptId != null) {
            wrapper.eq(Doctor::getDeptId, deptId);
        }
        if (StringUtils.hasText(jobTitle)) {
            wrapper.like(Doctor::getJobTitle, jobTitle);
        }
        wrapper.orderByDesc(Doctor::getCreateTime);

        Page<Doctor> page = new Page<>(pageNum, pageSize);
        IPage<Doctor> doctorPage = doctorService.page(page, wrapper);

        // 转换为公开 VO
        IPage<DoctorPublicVO> resultPage = doctorPage.convert(doctor -> {
            DoctorPublicVO vo = new DoctorPublicVO();
            BeanUtils.copyProperties(doctor, vo);
            // 关联查询科室名称
            try {
                DepartmentVO dept = departmentService.getDepartmentById(doctor.getDeptId());
                vo.setDeptName(dept.getDeptName());
            } catch (Exception e) {
                vo.setDeptName("未知科室");
            }
            return vo;
        });

        return Result.success(resultPage);
    }

    /**
     * 获取医生详情（公开信息）
     */
    @GetMapping("/{id}")
    public Result<DoctorPublicVO> getById(@PathVariable Long id) {
        Doctor doctor = doctorService.getById(id);
        if (doctor == null) {
            return Result.error("医生不存在");
        }

        DoctorPublicVO vo = new DoctorPublicVO();
        BeanUtils.copyProperties(doctor, vo);

        // 关联查询科室名称
        try {
            DepartmentVO dept = departmentService.getDepartmentById(doctor.getDeptId());
            vo.setDeptName(dept.getDeptName());
        } catch (Exception e) {
            vo.setDeptName("未知科室");
        }

        return Result.success(vo);
    }

    /**
     * 根据科室获取医生列表
     */
    @GetMapping("/by-department/{deptId}")
    public Result<IPage<DoctorPublicVO>> getByDepartment(
            @PathVariable Long deptId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return list(null, deptId, null, pageNum, pageSize);
    }
}