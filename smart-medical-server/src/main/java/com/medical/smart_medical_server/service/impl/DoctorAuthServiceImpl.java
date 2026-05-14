package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.smart_medical_server.DTO.DoctorLoginDTO;
import com.medical.smart_medical_server.DTO.DoctorPasswordUpdateDTO;
import com.medical.smart_medical_server.VO.DoctorLoginVO;
import com.medical.smart_medical_server.VO.DoctorProfileVO;
import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.DoctorMapper;
import com.medical.smart_medical_server.service.DoctorAuthService;
import com.medical.smart_medical_server.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DoctorAuthServiceImpl implements DoctorAuthService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public DoctorLoginVO login(DoctorLoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        if (username == null || password == null) {
            throw new BusinessException("用户名和密码不能为空");
        }

        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getUsername, username);
        Doctor doctor = doctorMapper.selectOne(wrapper);

        if (doctor == null) {
            throw new BusinessException("医生账号不存在");
        }

        if (!passwordEncoder.matches(password, doctor.getPassword())) {
            throw new BusinessException("密码错误");
        }

        String token = jwtUtil.generateToken(doctor.getId(), "doctor_" + doctor.getUsername());

        DoctorLoginVO vo = new DoctorLoginVO();
        vo.setToken(token);
        vo.setId(doctor.getId());
        vo.setUsername(doctor.getUsername());
        vo.setName(doctor.getName());
        vo.setDeptId(doctor.getDeptId());
        vo.setJobTitle(doctor.getJobTitle());
        vo.setPhone(doctor.getPhone());
        return vo;
    }

    @Override
    public DoctorProfileVO getProfile(String authHeader) {
        Long doctorId = getDoctorIdFromToken(authHeader);
        Doctor doctor = doctorMapper.selectById(doctorId);

        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        DoctorProfileVO vo = new DoctorProfileVO();
        BeanUtils.copyProperties(doctor, vo);
        return vo;
    }

    @Override
    public void updatePassword(String authHeader, DoctorPasswordUpdateDTO updateDTO) {
        Long doctorId = getDoctorIdFromToken(authHeader);
        String oldPassword = updateDTO.getOldPassword();
        String newPassword = updateDTO.getNewPassword();

        if (oldPassword == null || newPassword == null) {
            throw new BusinessException("旧密码和新密码不能为空");
        }

        if (newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        if (!passwordEncoder.matches(oldPassword, doctor.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctorMapper.updateById(doctor);
    }

    private Long getDoctorIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new BusinessException("无效的Token");
    }
}
