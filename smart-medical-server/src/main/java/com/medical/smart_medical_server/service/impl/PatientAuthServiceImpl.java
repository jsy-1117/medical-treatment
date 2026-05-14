package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.smart_medical_server.DTO.PatientLoginDTO;
import com.medical.smart_medical_server.DTO.PatientPasswordUpdateDTO;
import com.medical.smart_medical_server.DTO.PatientProfileUpdateDTO;
import com.medical.smart_medical_server.DTO.PatientRegisterDTO;
import com.medical.smart_medical_server.VO.PatientLoginVO;
import com.medical.smart_medical_server.VO.PatientProfileVO;
import com.medical.smart_medical_server.VO.PatientRegisterVO;
import com.medical.smart_medical_server.entity.Patient;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.PatientMapper;
import com.medical.smart_medical_server.service.PatientAuthService;
import com.medical.smart_medical_server.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientAuthServiceImpl implements PatientAuthService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public PatientLoginVO login(PatientLoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        if (username == null || password == null) {
            throw new BusinessException("用户名和密码不能为空");
        }

        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUsername, username);
        Patient patient = patientMapper.selectOne(wrapper);

        if (patient == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(password, patient.getPassword())) {
            throw new BusinessException("密码错误");
        }

        String token = jwtUtil.generateToken(patient.getId(), patient.getUsername());

        PatientLoginVO vo = new PatientLoginVO();
        vo.setToken(token);
        vo.setId(patient.getId());
        vo.setUsername(patient.getUsername());
        vo.setName(patient.getName());
        vo.setPhone(patient.getPhone());
        vo.setGender(patient.getGender());
        vo.setAge(patient.getAge());
        vo.setIdCard(patient.getIdCard());
        return vo;
    }

    @Override
    public PatientRegisterVO register(PatientRegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String name = registerDTO.getName();
        String phone = registerDTO.getPhone();

        if (username == null || password == null || name == null) {
            throw new BusinessException("用户名、密码、姓名不能为空");
        }

        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUsername, username);
        if (patientMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        Patient patient = new Patient();
        patient.setUsername(username);
        patient.setPassword(passwordEncoder.encode(password));
        patient.setName(name);
        patient.setPhone(phone);

        patientMapper.insert(patient);

        PatientRegisterVO vo = new PatientRegisterVO();
        vo.setId(patient.getId());
        vo.setUsername(patient.getUsername());
        vo.setName(patient.getName());
        return vo;
    }

    @Override
    public PatientProfileVO getProfile(Long patientId) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("用户不存在");
        }

        PatientProfileVO vo = new PatientProfileVO();
        BeanUtils.copyProperties(patient, vo);
        return vo;
    }

    @Override
    public PatientProfileVO updateProfile(Long patientId, PatientProfileUpdateDTO updateDTO) {
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("用户不存在");
        }

        if (updateDTO.getName() != null) {
            patient.setName(updateDTO.getName());
        }
        if (updateDTO.getPhone() != null) {
            patient.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getGender() != null) {
            patient.setGender(updateDTO.getGender());
        }
        if (updateDTO.getAge() != null) {
            patient.setAge(updateDTO.getAge());
        }
        if (updateDTO.getIdCard() != null) {
            patient.setIdCard(updateDTO.getIdCard());
        }

        patientMapper.updateById(patient);

        PatientProfileVO vo = new PatientProfileVO();
        BeanUtils.copyProperties(patient, vo);
        return vo;
    }

    @Override
    public void updatePassword(Long patientId, PatientPasswordUpdateDTO updateDTO) {
        String oldPassword = updateDTO.getOldPassword();
        String newPassword = updateDTO.getNewPassword();

        if (oldPassword == null || newPassword == null) {
            throw new BusinessException("旧密码和新密码不能为空");
        }

        if (newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }

        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, patient.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        patient.setPassword(passwordEncoder.encode(newPassword));
        patientMapper.updateById(patient);
    }
}
