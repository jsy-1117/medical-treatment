package com.medical.smart_medical_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smart_medical_server.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者 Mapper 接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    // MyBatis-Plus 已提供基础 CRUD 方法
    // 如需自定义 SQL，可在 mapper/PatientMapper.xml 中编写
}