package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medical.smart_medical_server.DTO.AdminLoginDTO;
import com.medical.smart_medical_server.DTO.AdminRegisterDTO;
import com.medical.smart_medical_server.DTO.AdminUpdateDTO;
import com.medical.smart_medical_server.entity.Admin;
import com.medical.smart_medical_server.exception.BusinessException;
import com.medical.smart_medical_server.mapper.AdminMapper;
import com.medical.smart_medical_server.service.AdminService;
import com.medical.smart_medical_server.utils.JwtUtil;
import com.medical.smart_medical_server.VO.AdminLoginVO;
import com.medical.smart_medical_server.VO.AdminVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AdminLoginVO login(AdminLoginDTO loginDTO) {
        // 1. 根据用户名查询管理员
        Admin admin = getByUsername(loginDTO.getUsername());
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 生成 JWT Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername());

        // 4. 构造返回对象
        AdminLoginVO loginVO = new AdminLoginVO();
        loginVO.setId(admin.getId());
        loginVO.setUsername(admin.getUsername());
        loginVO.setName(admin.getName());
        loginVO.setToken(token);

        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminVO register(AdminRegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        if (getByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建管理员实体
        Admin admin = new Admin();
        admin.setUsername(registerDTO.getUsername());
        admin.setPassword(passwordEncoder.encode(registerDTO.getPassword())); // 加密密码
        admin.setName(registerDTO.getName());

        // 3. 保存到数据库
        save(admin);

        // 4. 转换为 VO 返回
        return convertToVO(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminVO updateAdmin(Long id, AdminUpdateDTO updateDTO) {
        // 1. 查询管理员
        Admin admin = getById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 2. 更新信息
        if (updateDTO.getName() != null) {
            admin.setName(updateDTO.getName());
        }
        if (updateDTO.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        // 3. 保存更新
        updateById(admin);

        return convertToVO(admin);
    }

    @Override
    public Admin getByUsername(String username) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public AdminVO getCurrentAdmin(String token) {
        // 1. 验证 Token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "Token 无效或已过期");
        }

        // 2. 从 Token 中解析用户ID
        Long adminId = jwtUtil.getUserIdFromToken(token);

        // 3. 查询管理员信息
        Admin admin = getById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        return convertToVO(admin);
    }

    /**
     * 实体转 VO
     */
    private AdminVO convertToVO(Admin admin) {
        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        return vo;
    }
}