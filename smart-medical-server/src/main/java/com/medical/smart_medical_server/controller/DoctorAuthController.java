package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.DoctorMapper;
import com.medical.smart_medical_server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 医生认证控制器
 */
@RestController
@RequestMapping("/api/doctor-auth")
public class DoctorAuthController {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 医生登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || password == null) {
            throw new BusinessException("用户名和密码不能为空");
        }

        // 查询医生
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doctor::getUsername, username);
        Doctor doctor = doctorMapper.selectOne(wrapper);

        if (doctor == null) {
            throw new BusinessException("医生账号不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, doctor.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 生成 Token（使用 doctor_ 前缀区分）
        String token = jwtUtil.generateToken(doctor.getId(), "doctor_" + doctor.getUsername());

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("id", doctor.getId());
        result.put("username", doctor.getUsername());
        result.put("name", doctor.getName());
        result.put("deptId", doctor.getDeptId());
        result.put("jobTitle", doctor.getJobTitle());
        result.put("phone", doctor.getPhone());

        return Result.success(result);
    }

    /**
     * 获取当前医生信息
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authHeader) {
        Long doctorId = getDoctorIdFromToken(authHeader);
        Doctor doctor = doctorMapper.selectById(doctorId);

        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", doctor.getId());
        result.put("username", doctor.getUsername());
        result.put("name", doctor.getName());
        result.put("deptId", doctor.getDeptId());
        result.put("jobTitle", doctor.getJobTitle());
        result.put("introduction", doctor.getIntroduction());
        result.put("phone", doctor.getPhone());

        return Result.success(result);
    }

    private Long getDoctorIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new BusinessException("无效的Token");
    }

    /**
     * 医生修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestBody Map<String, String> data,
            @RequestHeader("Authorization") String authHeader) {
        Long doctorId = getDoctorIdFromToken(authHeader);
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");

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

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, doctor.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctorMapper.updateById(doctor);

        return Result.success(null);
    }
}