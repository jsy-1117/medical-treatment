package com.medical.smart_medical_server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.smart_medical_server.DTO.DepartmentCreateDTO;
import com.medical.smart_medical_server.DTO.DepartmentQueryDTO;
import com.medical.smart_medical_server.DTO.DepartmentUpdateDTO;
import com.medical.smart_medical_server.entity.Department;
import com.medical.smart_medical_server.VO.DepartmentVO;

import java.util.List;

/**
 * 科室服务接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 分页查询科室列表（管理员使用）
     */
    IPage<DepartmentVO> queryDepartmentPage(DepartmentQueryDTO queryDTO);

    /**
     * 获取所有科室列表（用户端使用，用于选择挂号科室）
     */
    List<DepartmentVO> getAllDepartments();

    /**
     * 根据ID获取科室详情
     */
    DepartmentVO getDepartmentById(Long id);

    /**
     * 创建科室
     */
    DepartmentVO createDepartment(DepartmentCreateDTO createDTO);

    /**
     * 更新科室
     */
    DepartmentVO updateDepartment(Long id, DepartmentUpdateDTO updateDTO);

    /**
     * 删除科室
     */
    void deleteDepartment(Long id);
}