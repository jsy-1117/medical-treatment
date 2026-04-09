package com.medical.smart_medical_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.smart_medical_server.DTO.AdminLoginDTO;
import com.medical.smart_medical_server.DTO.AdminRegisterDTO;
import com.medical.smart_medical_server.DTO.AdminUpdateDTO;
import com.medical.smart_medical_server.entity.Admin;
import com.medical.smart_medical_server.VO.AdminVO;
import com.medical.smart_medical_server.VO.AdminLoginVO;

/**
 * 管理员服务接口
 */
public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     * @param loginDTO 登录信息
     * @return 登录响应（包含 Token）
     */
    AdminLoginVO login(AdminLoginDTO loginDTO);

    /**
     * 管理员注册
     * @param registerDTO 注册信息
     * @return 管理员信息
     */
    AdminVO register(AdminRegisterDTO registerDTO);

    /**
     * 更新管理员信息
     * @param id 管理员ID
     * @param updateDTO 更新信息
     * @return 更新后的管理员信息
     */
    AdminVO updateAdmin(Long id, AdminUpdateDTO updateDTO);

    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员信息
     */
    Admin getByUsername(String username);

    /**
     * 根据 Token 获取当前管理员信息
     * @param token JWT Token
     * @return 管理员信息
     */
    AdminVO getCurrentAdmin(String token);
}