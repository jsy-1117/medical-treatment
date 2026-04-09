package com.medical.smart_medical_server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.smart_medical_server.DTO.AppointmentCreateDTO;
import com.medical.smart_medical_server.DTO.AppointmentQueryDTO;
import com.medical.smart_medical_server.entity.Appointment;
import com.medical.smart_medical_server.VO.AppointmentVO;

/**
 * 挂号服务接口
 */
public interface AppointmentService extends IService<Appointment> {

    /**
     * 分页查询挂号记录（管理员使用）
     */
    IPage<AppointmentVO> queryAppointmentPage(AppointmentQueryDTO queryDTO);

    /**
     * 根据ID获取挂号详情
     */
    AppointmentVO getAppointmentById(Long id);

    /**
     * 创建挂号（用户端挂号）
     */
    AppointmentVO createAppointment(AppointmentCreateDTO createDTO);

    /**
     * 更新挂号状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 取消挂号（用户端取消）
     */
    void cancelAppointment(Long id, Long patientId);

    /**
     * 删除挂号记录（管理员使用）
     */
    void deleteAppointment(Long id);

    /**
     * 查询患者的挂号记录
     */
    IPage<AppointmentVO> queryPatientAppointments(Long patientId, Integer pageNum, Integer pageSize);
}