package com.medical.smart_medical_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smart_medical_server.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员 Mapper 接口
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    // MyBatis-Plus 已提供基础 CRUD 方法
    // 如需自定义 SQL，可在 mapper/AdminMapper.xml 中编写
}