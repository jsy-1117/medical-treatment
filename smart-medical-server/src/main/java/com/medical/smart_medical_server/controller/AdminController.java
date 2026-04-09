package com.medical.smart_medical_server.controller;

import com.medical.smart_medical_server.DTO.AdminLoginDTO;
import com.medical.smart_medical_server.DTO.AdminRegisterDTO;
import com.medical.smart_medical_server.DTO.AdminUpdateDTO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.AdminService;
import com.medical.smart_medical_server.VO.AdminLoginVO;
import com.medical.smart_medical_server.VO.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import com.medical.smart_medical_server.entity.Admin;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.AdminMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Validated @RequestBody AdminLoginDTO loginDTO) {
        AdminLoginVO loginVO = adminService.login(loginDTO);
        return Result.success(loginVO);
    }

    /**
     * 管理员注册
     */
    @PostMapping("/register")
    public Result<AdminVO> register(@Validated @RequestBody AdminRegisterDTO registerDTO) {
        AdminVO adminVO = adminService.register(registerDTO);
        return Result.success(adminVO);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/current")
    public Result<AdminVO> getCurrentAdmin(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        AdminVO adminVO = adminService.getCurrentAdmin(jwtToken);
        return Result.success(adminVO);
    }

    /**
     * 更新管理员信息
     */
    @PutMapping("/{id}")
    public Result<AdminVO> updateAdmin(
            @PathVariable Long id,
            @Validated @RequestBody AdminUpdateDTO updateDTO) {
        AdminVO adminVO = adminService.updateAdmin(id, updateDTO);
        return Result.success(adminVO);
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        adminService.removeById(id);
        return Result.success();
    }

    /**
     * 管理员修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            throw new BusinessException("旧密码和新密码不能为空");
        }

        if (newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminMapper.updateById(admin);

        return Result.success(null);
    }
}