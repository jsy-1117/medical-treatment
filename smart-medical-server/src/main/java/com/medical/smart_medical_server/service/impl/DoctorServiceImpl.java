package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.smart_medical_server.DTO.DoctorCreateDTO;
import com.medical.smart_medical_server.DTO.DoctorQueryDTO;
import com.medical.smart_medical_server.DTO.DoctorUpdateDTO;
import com.medical.smart_medical_server.entity.Department;
import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.DepartmentMapper;
import com.medical.smart_medical_server.mapper.DoctorMapper;
import com.medical.smart_medical_server.service.DoctorService;
import com.medical.smart_medical_server.VO.DoctorVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生服务实现类
 */
@Service
@CacheConfig(cacheNames = "doctor")
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    @Cacheable(key = "'page:' + #queryDTO.pageNum + ':' + #queryDTO.pageSize + ':' + #queryDTO.name + ':' + #queryDTO.deptId", unless = "#result == null")
    public IPage<DoctorVO> queryDoctorPage(DoctorQueryDTO queryDTO) {
        Page<Doctor> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(Doctor::getName, queryDTO.getName());
        }

        if (queryDTO.getDeptId() != null) {
            wrapper.eq(Doctor::getDeptId, queryDTO.getDeptId());
        }

        if (queryDTO.getJobTitle() != null && !queryDTO.getJobTitle().isEmpty()) {
            wrapper.like(Doctor::getJobTitle, queryDTO.getJobTitle());
        }

        wrapper.orderByDesc(Doctor::getCreateTime);

        IPage<Doctor> doctorPage = page(page, wrapper);

        // 转换为 VO
        Page<DoctorVO> voPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), doctorPage.getTotal());
        List<DoctorVO> voList = doctorPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Cacheable(key = "'id:' + #id", unless = "#result == null")
    public DoctorVO getDoctorById(Long id) {
        Doctor doctor = getById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }
        return convertToVO(doctor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public DoctorVO createDoctor(DoctorCreateDTO createDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getUsername, createDTO.getUsername());
        if (count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(createDTO, doctor);

        // 加密密码
        doctor.setPassword(passwordEncoder.encode(createDTO.getPassword()));

        save(doctor);
        return convertToVO(doctor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public DoctorVO updateDoctor(Long id, DoctorUpdateDTO updateDTO) {
        Doctor existDoctor = getById(id);
        if (existDoctor == null) {
            throw new BusinessException("医生不存在");
        }

        // 更新非空字段
        if (updateDTO.getDeptId() != null) {
            existDoctor.setDeptId(updateDTO.getDeptId());
        }
        if (updateDTO.getName() != null) {
            existDoctor.setName(updateDTO.getName());
        }
        if (updateDTO.getJobTitle() != null) {
            existDoctor.setJobTitle(updateDTO.getJobTitle());
        }
        if (updateDTO.getIntroduction() != null) {
            existDoctor.setIntroduction(updateDTO.getIntroduction());
        }
        if (updateDTO.getPhone() != null) {
            existDoctor.setPhone(updateDTO.getPhone());
        }

        // 如果更新了密码,需要加密
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            existDoctor.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        updateById(existDoctor);
        return convertToVO(existDoctor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public void deleteDoctor(Long id) {
        Doctor doctor = getById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }
        removeById(id);
    }

    @Override
    public Doctor getByUsername(String username) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getUsername, username);
        return getOne(wrapper);
    }

    private DoctorVO convertToVO(Doctor doctor) {
        DoctorVO vo = new DoctorVO();
        BeanUtils.copyProperties(doctor, vo);

        // 关联查询科室名称
        if (doctor.getDeptId() != null) {
            Department department = departmentMapper.selectById(doctor.getDeptId());
            if (department != null) {
                vo.setDeptName(department.getDeptName());
            }
        }

        return vo;
    }
}