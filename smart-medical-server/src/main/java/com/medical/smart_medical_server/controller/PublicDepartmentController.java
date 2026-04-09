package com.medical.smart_medical_server.controller;

import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.DepartmentService;
import com.medical.smart_medical_server.VO.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室公开接口(用户端使用，无需认证)
 */
@RestController
@RequestMapping("/api/public/department")
public class PublicDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取所有科室列表（用于用户选择挂号科室）
     * 注意：此路由必须在 /{id} 之前，否则会被识别为 id
     */
    @GetMapping("/all")
    public Result<List<DepartmentVO>> all() {
        List<DepartmentVO> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }

    /**
     * 获取所有科室列表（备用接口）
     */
    @GetMapping("/list")
    public Result<List<DepartmentVO>> list() {
        List<DepartmentVO> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }

    /**
     * 获取科室详情
     * 注意：此路由必须在最后，避免匹配 /all 等具体路径
     */
    @GetMapping("/{id}")
    public Result<DepartmentVO> getById(@PathVariable Long id) {
        DepartmentVO departmentVO = departmentService.getDepartmentById(id);
        return Result.success(departmentVO);
    }
}