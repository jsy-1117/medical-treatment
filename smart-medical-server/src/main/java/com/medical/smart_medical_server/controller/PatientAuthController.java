package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.entity.Patient;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.PatientMapper;
import com.medical.smart_medical_server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 患者认证控制器
 */
@RestController
@RequestMapping("/api/patient")
public class PatientAuthController {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 患者登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || password == null) {
            throw new BusinessException("用户名和密码不能为空");
        }

        // 查询用户
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUsername, username);
        Patient patient = patientMapper.selectOne(wrapper);

        if (patient == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, patient.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(patient.getId(), patient.getUsername());

        // 返回结果
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("id", patient.getId());
        result.put("username", patient.getUsername());
        result.put("name", patient.getName());
        result.put("phone", patient.getPhone());
        result.put("gender", patient.getGender());      // ✅ 添加性别
        result.put("age", patient.getAge());            // ✅ 添加年龄
        result.put("idCard", patient.getIdCard());      // ✅ 添加身份证

        return Result.success(result);
    }

    /**
     * 患者注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> registerData) {
        String username = registerData.get("username");
        String password = registerData.get("password");
        String name = registerData.get("name");
        String phone = registerData.get("phone");

        if (username == null || password == null || name == null) {
            throw new BusinessException("用户名、密码、姓名不能为空");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getUsername, username);
        if (patientMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        Patient patient = new Patient();
        patient.setUsername(username);
        patient.setPassword(passwordEncoder.encode(password));
        patient.setName(name);
        patient.setPhone(phone);

        patientMapper.insert(patient);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", patient.getId());
        result.put("username", patient.getUsername());
        result.put("name", patient.getName());

        return Result.success(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(HttpServletRequest request) {
        // 从 Token 中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        Patient patient = patientMapper.selectById(userId);

        if (patient == null) {
            throw new BusinessException("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", patient.getId());
        result.put("username", patient.getUsername());
        result.put("name", patient.getName());
        result.put("phone", patient.getPhone());
        result.put("gender", patient.getGender());
        result.put("age", patient.getAge());
        result.put("idCard", patient.getIdCard());

        return Result.success(result);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<Map<String, Object>> updateProfile(
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Patient patient = patientMapper.selectById(userId);

        if (patient == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新字段
        if (data.get("name") != null) {
            patient.setName((String) data.get("name"));
        }
        if (data.get("phone") != null) {
            patient.setPhone((String) data.get("phone"));
        }
        if (data.get("gender") != null) {
            patient.setGender((Integer) data.get("gender"));
        }
        if (data.get("age") != null) {
            patient.setAge((Integer) data.get("age"));
        }
        if (data.get("idCard") != null) {
            patient.setIdCard((String) data.get("idCard"));
        }

        patientMapper.updateById(patient);

        // 返回更新后的信息
        Map<String, Object> result = new HashMap<>();
        result.put("id", patient.getId());
        result.put("username", patient.getUsername());
        result.put("name", patient.getName());
        result.put("phone", patient.getPhone());
        result.put("gender", patient.getGender());
        result.put("age", patient.getAge());
        result.put("idCard", patient.getIdCard());

        return Result.success(result);
    }

    /**
     * 患者修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        Long patientId = (Long) request.getAttribute("userId");
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");

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

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, patient.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        patient.setPassword(passwordEncoder.encode(newPassword));
        patientMapper.updateById(patient);

        return Result.success(null);
    }
}