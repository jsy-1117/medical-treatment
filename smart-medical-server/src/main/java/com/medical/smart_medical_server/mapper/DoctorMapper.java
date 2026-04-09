package com.medical.smart_medical_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smart_medical_server.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生 Mapper 接口
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
}