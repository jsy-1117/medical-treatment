package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.smart_medical_server.DTO.PatientCreateDTO;
import com.medical.smart_medical_server.DTO.PatientQueryDTO;
import com.medical.smart_medical_server.DTO.PatientUpdateDTO;
import com.medical.smart_medical_server.entity.Patient;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.PatientMapper;
import com.medical.smart_medical_server.service.PatientService;
import com.medical.smart_medical_server.VO.PatientVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 患者服务实现类
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public IPage<PatientVO> queryPatientPage(PatientQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(Patient::getUsername, queryDTO.getUsername());
        }
        // 姓名模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Patient::getName, queryDTO.getName());
        }
        // 手机号精确查询
        if (StringUtils.hasText(queryDTO.getPhone())) {
            wrapper.eq(Patient::getPhone, queryDTO.getPhone());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Patient::getCreateTime);

        // 分页查询
        Page<Patient> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Patient> patientPage = page(page, wrapper);

        // 转换为 VO
        return patientPage.convert(this::convertToVO);
    }

    @Override
    public PatientVO getPatientById(Long id) {
        Patient patient = getById(id);
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }
        return convertToVO(patient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatientVO createPatient(PatientCreateDTO createDTO) {
        // 1. 检查用户名是否已存在
        if (getByUsername(createDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建患者实体
        Patient patient = new Patient();
        BeanUtils.copyProperties(createDTO, patient);
        patient.setPassword(passwordEncoder.encode(createDTO.getPassword())); // 加密密码

        // 3. 保存到数据库
        save(patient);

        // 4. 转换为 VO 返回
        return convertToVO(patient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatientVO updatePatient(Long id, PatientUpdateDTO updateDTO) {
        // 1. 查询患者
        Patient patient = getById(id);
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }

        // 2. 更新信息
        if (StringUtils.hasText(updateDTO.getName())) {
            patient.setName(updateDTO.getName());
        }
        if (StringUtils.hasText(updateDTO.getPassword())) {
            patient.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        if (updateDTO.getGender() != null) {
            patient.setGender(updateDTO.getGender());
        }
        if (updateDTO.getAge() != null) {
            patient.setAge(updateDTO.getAge());
        }
        if (StringUtils.hasText(updateDTO.getPhone())) {
            patient.setPhone(updateDTO.getPhone());
        }
        if (StringUtils.hasText(updateDTO.getIdCard())) {
            patient.setIdCard(updateDTO.getIdCard());
        }

        // 3. 保存更新
        updateById(patient);

        return convertToVO(patient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePatient(Long id) {
        Patient patient = getById(id);
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }
        removeById(id);
    }

    @Override
    public Patient getByUsername(String username) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUsername, username);
        return getOne(wrapper);
    }

    /**
     * 实体转 VO
     */
    private PatientVO convertToVO(Patient patient) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo);
        return vo;
    }
}