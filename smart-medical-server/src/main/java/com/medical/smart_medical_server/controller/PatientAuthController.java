package com.medical.smart_medical_server.controller;

import com.medical.smart_medical_server.DTO.PatientLoginDTO;
import com.medical.smart_medical_server.DTO.PatientPasswordUpdateDTO;
import com.medical.smart_medical_server.DTO.PatientProfileUpdateDTO;
import com.medical.smart_medical_server.DTO.PatientRegisterDTO;
import com.medical.smart_medical_server.VO.PatientLoginVO;
import com.medical.smart_medical_server.VO.PatientProfileVO;
import com.medical.smart_medical_server.VO.PatientRegisterVO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.PatientAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 患者认证控制器
 */
@RestController
@RequestMapping("/api/patient")
public class PatientAuthController {

    @Autowired
    private PatientAuthService patientAuthService;

    /**
     * 患者登录
     */
    @PostMapping("/login")
    public Result<PatientLoginVO> login(@RequestBody @Validated PatientLoginDTO loginDTO) {
        PatientLoginVO result = patientAuthService.login(loginDTO);
        return Result.success(result);
    }

    /**
     * 患者注册
     */
    @PostMapping("/register")
    public Result<PatientRegisterVO> register(@RequestBody @Validated PatientRegisterDTO registerDTO) {
        PatientRegisterVO result = patientAuthService.register(registerDTO);
        return Result.success(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<PatientProfileVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PatientProfileVO result = patientAuthService.getProfile(userId);
        return Result.success(result);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<PatientProfileVO> updateProfile(
            @RequestBody PatientProfileUpdateDTO updateDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PatientProfileVO result = patientAuthService.updateProfile(userId, updateDTO);
        return Result.success(result);
    }

    /**
     * 患者修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestBody PatientPasswordUpdateDTO updateDTO,
            HttpServletRequest request) {
        Long patientId = (Long) request.getAttribute("userId");
        patientAuthService.updatePassword(patientId, updateDTO);
        return Result.success(null);
    }
}
