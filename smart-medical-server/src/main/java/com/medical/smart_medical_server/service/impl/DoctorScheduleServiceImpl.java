package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.smart_medical_server.DTO.ScheduleCreateDTO;
import com.medical.smart_medical_server.DTO.ScheduleQueryDTO;
import com.medical.smart_medical_server.entity.Department;
import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.entity.DoctorSchedule;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.DepartmentMapper;
import com.medical.smart_medical_server.mapper.DoctorMapper;
import com.medical.smart_medical_server.mapper.DoctorScheduleMapper;
import com.medical.smart_medical_server.service.DoctorScheduleService;
import com.medical.smart_medical_server.VO.DoctorScheduleVO;
import com.medical.smart_medical_server.VO.ScheduleLLMVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;                    // ✅ 添加这行
import java.util.stream.Collectors;

/**
 * 医生排班服务实现类
 */
@Service
@CacheConfig(cacheNames = "schedule")
public class DoctorScheduleServiceImpl extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> implements DoctorScheduleService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    @Cacheable(key = "'page:' + #queryDTO.pageNum + ':' + #queryDTO.pageSize + ':' + #queryDTO.doctorId + ':' + #queryDTO.deptId", unless = "#result == null")
    public IPage<DoctorScheduleVO> querySchedulePage(ScheduleQueryDTO queryDTO) {
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getDoctorId() != null) {
            wrapper.eq(DoctorSchedule::getDoctorId, queryDTO.getDoctorId());
        }
        if (queryDTO.getDeptId() != null) {
            wrapper.eq(DoctorSchedule::getDeptId, queryDTO.getDeptId());
        }
        if (queryDTO.getStartDate() != null) {
            wrapper.ge(DoctorSchedule::getWorkDate, queryDTO.getStartDate());
        }
        if (queryDTO.getEndDate() != null) {
            wrapper.le(DoctorSchedule::getWorkDate, queryDTO.getEndDate());
        }

        wrapper.orderByDesc(DoctorSchedule::getWorkDate);

        Page<DoctorSchedule> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<DoctorSchedule> schedulePage = page(page, wrapper);

        return schedulePage.convert(this::convertToVO);
    }

    @Override
    @Cacheable(key = "'id:' + #id", unless = "#result == null")
    public DoctorScheduleVO getScheduleById(Long id) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        return convertToVO(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public DoctorScheduleVO createSchedule(ScheduleCreateDTO createDTO) {
        // 检查是否已存在相同的排班
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorSchedule::getDoctorId, createDTO.getDoctorId())
                .eq(DoctorSchedule::getWorkDate, createDTO.getWorkDate())
                .eq(DoctorSchedule::getShiftType, createDTO.getShiftType());
        if (count(wrapper) > 0) {
            throw new BusinessException("该时段已存在排班");
        }

        DoctorSchedule schedule = new DoctorSchedule();
        BeanUtils.copyProperties(createDTO, schedule);
        schedule.setRemainingQuota(createDTO.getQuota());
        schedule.setStatus(1);

        save(schedule);

        return convertToVO(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public void updateScheduleStatus(Long id, Integer status) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        schedule.setStatus(status);
        updateById(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public void deleteSchedule(Long id) {
        DoctorSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        removeById(id);
    }

    /**
     * ✅ 新增：获取近期可预约排班（用于 LLM 上下文）
     * 缓存时间：5分钟（在 llmContext 缓存中）
     */
    @Override
    @Cacheable(cacheNames = "llmContext", key = "'schedules:' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<ScheduleLLMVO> getAvailableSchedulesForLLM(int limit) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);

        // 查询排班列表
        LambdaQueryWrapper<DoctorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(DoctorSchedule::getWorkDate, today)
                .le(DoctorSchedule::getWorkDate, endDate)
                .eq(DoctorSchedule::getStatus, 1)
                .gt(DoctorSchedule::getRemainingQuota, 0)
                .orderByAsc(DoctorSchedule::getWorkDate)
                .orderByAsc(DoctorSchedule::getShiftType)
                .last("LIMIT " + limit);

        List<DoctorSchedule> schedules = list(wrapper);

        if (schedules.isEmpty()) {
            return List.of();
        }

        // ✅ 优化：批量查询所有相关的医生ID
        Set<Long> doctorIds = schedules.stream()
                .map(DoctorSchedule::getDoctorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Doctor> doctorMap = new HashMap<>();
        if (!doctorIds.isEmpty()) {
            List<Doctor> doctors = doctorMapper.selectBatchIds(doctorIds);
            doctorMap = doctors.stream()
                    .collect(Collectors.toMap(Doctor::getId, d -> d));
        }

        // ✅ 优化：批量查询所有相关的科室ID
        Set<Long> deptIds = schedules.stream()
                .map(DoctorSchedule::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Department> deptMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            List<Department> depts = departmentMapper.selectBatchIds(deptIds);
            deptMap = depts.stream()
                    .collect(Collectors.toMap(Department::getId, d -> d));
        }

        // 转换为 VO
        final Map<Long, Doctor> finalDoctorMap = doctorMap;
        final Map<Long, Department> finalDeptMap = deptMap;

        return schedules.stream()
                .map(schedule -> {
                    ScheduleLLMVO vo = ScheduleLLMVO.builder()
                            .workDate(schedule.getWorkDate())
                            .shiftType(schedule.getShiftType())
                            .remainingQuota(schedule.getRemainingQuota())
                            .status(schedule.getStatus())
                            .build();

                    Doctor doctor = finalDoctorMap.get(schedule.getDoctorId());
                    if (doctor != null) {
                        vo.setDoctorName(doctor.getName());
                        vo.setDoctorJobTitle(doctor.getJobTitle());
                    }

                    Department dept = finalDeptMap.get(schedule.getDeptId());
                    if (dept != null) {
                        vo.setDeptName(dept.getDeptName());
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为普通 VO
     */
    private DoctorScheduleVO convertToVO(DoctorSchedule schedule) {
        DoctorScheduleVO vo = new DoctorScheduleVO();
        BeanUtils.copyProperties(schedule, vo);

        // 关联查询医生和科室信息
        if (schedule.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null) {
                vo.setDoctorName(doctor.getName());
                vo.setDoctorJobTitle(doctor.getJobTitle());
            }
        }
        if (schedule.getDeptId() != null) {
            Department dept = departmentMapper.selectById(schedule.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        return vo;
    }

    /**
     * ✅ 新增：转换为 LLM 专用 VO（仅公开信息，不含 ID）
     */
    private ScheduleLLMVO convertToLLMVO(DoctorSchedule schedule) {
        ScheduleLLMVO vo = ScheduleLLMVO.builder()
                .workDate(schedule.getWorkDate())
                .shiftType(schedule.getShiftType())
                .remainingQuota(schedule.getRemainingQuota())
                .status(schedule.getStatus())
                .build();

        // 关联查询医生信息（只取公开字段）
        if (schedule.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());
            if (doctor != null) {
                vo.setDoctorName(doctor.getName());           // ✅ 姓名
                vo.setDoctorJobTitle(doctor.getJobTitle());   // ✅ 职称
                // ❌ 不包含：username, phone, password
            }
        }

        // 关联查询科室名称
        if (schedule.getDeptId() != null) {
            Department dept = departmentMapper.selectById(schedule.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());           // ✅ 科室名
                // ❌ 不包含：id
            }
        }

        return vo;
    }
}