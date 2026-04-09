package com.medical.smart_medical_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smart_medical_server.entity.Appointment;
import com.medical.smart_medical_server.VO.AppointmentVO;
import com.medical.smart_medical_server.DTO.AppointmentQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 挂号记录 Mapper 接口
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    /**
     * 分页查询挂号记录（带关联信息）
     */
    IPage<AppointmentVO> selectAppointmentPage(Page<AppointmentVO> page, @Param("query") AppointmentQueryDTO queryDTO);
}