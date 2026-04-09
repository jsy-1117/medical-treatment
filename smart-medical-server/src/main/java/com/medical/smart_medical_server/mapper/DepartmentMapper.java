package com.medical.smart_medical_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smart_medical_server.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科室 Mapper 接口
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}