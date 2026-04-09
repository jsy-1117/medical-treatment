package com.medical.smart_medical_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smart_medical_server.entity.Diagnosis;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiagnosisMapper extends BaseMapper<Diagnosis> {
}