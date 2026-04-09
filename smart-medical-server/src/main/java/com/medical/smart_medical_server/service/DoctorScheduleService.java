package com.medical.smart_medical_server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.smart_medical_server.DTO.ScheduleCreateDTO;
import com.medical.smart_medical_server.DTO.ScheduleQueryDTO;
import com.medical.smart_medical_server.entity.DoctorSchedule;
import com.medical.smart_medical_server.VO.DoctorScheduleVO;
import com.medical.smart_medical_server.VO.ScheduleLLMVO;

import java.util.List;

/**
 * 医生排班服务接口
 */
public interface DoctorScheduleService extends IService<DoctorSchedule> {

    IPage<DoctorScheduleVO> querySchedulePage(ScheduleQueryDTO queryDTO);

    DoctorScheduleVO getScheduleById(Long id);

    DoctorScheduleVO createSchedule(ScheduleCreateDTO createDTO);

    void updateScheduleStatus(Long id, Integer status);

    void deleteSchedule(Long id);

    List<ScheduleLLMVO> getAvailableSchedulesForLLM(int limit);
}