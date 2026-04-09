package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.DepartmentCreateDTO;
import com.medical.smart_medical_server.DTO.DepartmentQueryDTO;
import com.medical.smart_medical_server.DTO.DepartmentUpdateDTO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.DepartmentService;
import com.medical.smart_medical_server.VO.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室管理控制器
 */
@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 分页查询科室列表（管理员使用）
     */
    @GetMapping("/list")
    public Result<IPage<DepartmentVO>> list(DepartmentQueryDTO queryDTO) {
        IPage<DepartmentVO> page = departmentService.queryDepartmentPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取所有科室列表（用户端使用，用于选择挂号科室）
     */
    @GetMapping("/all")
    public Result<List<DepartmentVO>> all() {
        List<DepartmentVO> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }

    /**
     * 根据ID获取科室详情
     */
    @GetMapping("/{id}")
    public Result<DepartmentVO> getById(@PathVariable Long id) {
        DepartmentVO departmentVO = departmentService.getDepartmentById(id);
        return Result.success(departmentVO);
    }

    /**
     * 创建科室（管理员使用）
     */
    @PostMapping
    public Result<DepartmentVO> create(@Validated @RequestBody DepartmentCreateDTO createDTO) {
        DepartmentVO departmentVO = departmentService.createDepartment(createDTO);
        return Result.success(departmentVO);
    }

    /**
     * 更新科室（管理员使用）
     */
    @PutMapping("/{id}")
    public Result<DepartmentVO> update(
            @PathVariable Long id,
            @Validated @RequestBody DepartmentUpdateDTO updateDTO) {
        DepartmentVO departmentVO = departmentService.updateDepartment(id, updateDTO);
        return Result.success(departmentVO);
    }

    /**
     * 删除科室（管理员使用）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }
}