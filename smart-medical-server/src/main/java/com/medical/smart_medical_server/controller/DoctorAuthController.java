package com.medical.smart_medical_server.controller;

import com.medical.smart_medical_server.DTO.DoctorLoginDTO;
import com.medical.smart_medical_server.DTO.DoctorPasswordUpdateDTO;
import com.medical.smart_medical_server.VO.DoctorLoginVO;
import com.medical.smart_medical_server.VO.DoctorProfileVO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.DoctorAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生认证控制器
 */
@RestController
@RequestMapping("/api/doctor-auth")
public class DoctorAuthController {

    @Autowired
    private DoctorAuthService doctorAuthService;

    /**
     * 医生登录
     */
    @PostMapping("/login")
    public Result<DoctorLoginVO> login(@RequestBody @Validated DoctorLoginDTO loginDTO) {
        DoctorLoginVO result = doctorAuthService.login(loginDTO);
        return Result.success(result);
    }

    /**
     * 获取当前医生信息
     */
    @GetMapping("/profile")
    public Result<DoctorProfileVO> getProfile(@RequestHeader("Authorization") String authHeader) {
        DoctorProfileVO result = doctorAuthService.getProfile(authHeader);
        return Result.success(result);
    }

    /**
     * 医生修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestBody @Validated DoctorPasswordUpdateDTO updateDTO,
            @RequestHeader("Authorization") String authHeader) {
        doctorAuthService.updatePassword(authHeader, updateDTO);
        return Result.success(null);
    }
}
