package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.smart_medical_server.DTO.AppointmentCreateDTO;
import com.medical.smart_medical_server.DTO.AppointmentQueryDTO;
import com.medical.smart_medical_server.entity.Appointment;
import com.medical.smart_medical_server.entity.DoctorSchedule;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.AppointmentMapper;
import com.medical.smart_medical_server.mapper.DoctorScheduleMapper;
import com.medical.smart_medical_server.service.AppointmentService;
import com.medical.smart_medical_server.VO.AppointmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 挂号服务实现类
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Autowired
    private DoctorScheduleMapper scheduleMapper;

    @Override
    public IPage<AppointmentVO> queryAppointmentPage(AppointmentQueryDTO queryDTO) {
        Page<AppointmentVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return baseMapper.selectAppointmentPage(page, queryDTO);
    }

    @Override
    public AppointmentVO getAppointmentById(Long id) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException("挂号记录不存在");
        }
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO createAppointment(AppointmentCreateDTO createDTO) {
        // 1. 检查排班是否存在且有剩余号源
        DoctorSchedule schedule = scheduleMapper.selectById(createDTO.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        if (schedule.getStatus() != 1) {
            throw new BusinessException("该时段已停诊");
        }
        if (schedule.getRemainingQuota() <= 0) {
            throw new BusinessException("该时段号源已满");
        }

        // 2. 检查是否已挂过号
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getPatientId, createDTO.getPatientId())
                .eq(Appointment::getScheduleId, createDTO.getScheduleId())
                .ne(Appointment::getStatus, 2); // 排除已取消的
        if (count(wrapper) > 0) {
            throw new BusinessException("您已预约过该时段");
        }

        // 3. 创建挂号记录
        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(createDTO, appointment);
        appointment.setStatus(0); // 待就诊
        save(appointment);

        // 4. 减少剩余号源
        schedule.setRemainingQuota(schedule.getRemainingQuota() - 1);
        scheduleMapper.updateById(schedule);

        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException("挂号记录不存在");
        }
        appointment.setStatus(status);
        updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(Long id, Long patientId) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException("挂号记录不存在");
        }
        // 验证是本人的挂号
        if (!appointment.getPatientId().equals(patientId)) {
            throw new BusinessException("无权取消此挂号");
        }
        if (appointment.getStatus() != 0) {
            throw new BusinessException("只能取消待就诊的挂号");
        }

        // 更新状态为已取消
        appointment.setStatus(2);
        updateById(appointment);

        // 恢复号源
        DoctorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
        if (schedule != null) {
            schedule.setRemainingQuota(schedule.getRemainingQuota() + 1);
            scheduleMapper.updateById(schedule);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAppointment(Long id) {
        Appointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException("挂号记录不存在");
        }
        removeById(id);
    }

    @Override
    public IPage<AppointmentVO> queryPatientAppointments(Long patientId, Integer pageNum, Integer pageSize) {
        AppointmentQueryDTO queryDTO = new AppointmentQueryDTO();
        queryDTO.setPatientId(patientId);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return queryAppointmentPage(queryDTO);
    }
}