package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.smart_medical_server.DTO.DepartmentCreateDTO;
import com.medical.smart_medical_server.DTO.DepartmentQueryDTO;
import com.medical.smart_medical_server.DTO.DepartmentUpdateDTO;
import com.medical.smart_medical_server.entity.Department;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.DepartmentMapper;
import com.medical.smart_medical_server.service.DepartmentService;
import com.medical.smart_medical_server.VO.DepartmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 科室服务实现类
 */
@Service
@CacheConfig(cacheNames = "department")
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    @Cacheable(key = "'page:' + #queryDTO.pageNum + ':' + #queryDTO.pageSize + ':' + #queryDTO.deptName", unless = "#result == null")
    public IPage<DepartmentVO> queryDepartmentPage(DepartmentQueryDTO queryDTO) {
        Page<Department> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getDeptName() != null && !queryDTO.getDeptName().isEmpty()) {
            wrapper.like(Department::getDeptName, queryDTO.getDeptName());
        }

        wrapper.orderByDesc(Department::getCreateTime);

        IPage<Department> departmentPage = page(page, wrapper);

        // 转换为 VO
        Page<DepartmentVO> voPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), departmentPage.getTotal());
        List<DepartmentVO> voList = departmentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Cacheable(key = "'id:' + #id", unless = "#result == null")
    public DepartmentVO getDepartmentById(Long id) {
        Department department = getById(id);
        if (department == null) {
            throw new BusinessException("科室不存在");
        }
        return convertToVO(department);
    }

    @Override
    @Cacheable(key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<DepartmentVO> getAllDepartments() {
        List<Department> departments = list();
        return departments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public DepartmentVO createDepartment(DepartmentCreateDTO createDTO) {
        // 检查科室名称是否已存在
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDeptName, createDTO.getDeptName());
        if (count(wrapper) > 0) {
            throw new BusinessException("科室名称已存在");
        }

        Department department = new Department();
        BeanUtils.copyProperties(createDTO, department);
        save(department);

        return convertToVO(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public DepartmentVO updateDepartment(Long id, DepartmentUpdateDTO updateDTO) {
        Department existDepartment = getById(id);
        if (existDepartment == null) {
            throw new BusinessException("科室不存在");
        }

        // 检查科室名称是否与其他科室重复
        if (updateDTO.getDeptName() != null && !updateDTO.getDeptName().isEmpty()) {
            LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Department::getDeptName, updateDTO.getDeptName())
                    .ne(Department::getId, id);
            if (count(wrapper) > 0) {
                throw new BusinessException("科室名称已存在");
            }
        }

        // 更新非空字段
        if (updateDTO.getDeptName() != null) {
            existDepartment.setDeptName(updateDTO.getDeptName());
        }
        if (updateDTO.getDeptDesc() != null) {
            existDepartment.setDeptDesc(updateDTO.getDeptDesc());
        }
        if (updateDTO.getLocation() != null) {
            existDepartment.setLocation(updateDTO.getLocation());
        }

        updateById(existDepartment);
        return convertToVO(existDepartment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public void deleteDepartment(Long id) {
        Department department = getById(id);
        if (department == null) {
            throw new BusinessException("科室不存在");
        }
        // TODO: 检查是否有医生关联此科室
        removeById(id);
    }

    private DepartmentVO convertToVO(Department department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);
        return vo;
    }
}