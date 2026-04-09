package com.medical.smart_medical_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 医生信息实体类
 */
@Data
@TableName("doctor")
public class Doctor {

    /**
     * 医生ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属科室ID
     */
    private Long deptId;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 登录密码（加密存储）
     */
    private String password;

    /**
     * 医生姓名
     */
    private String name;

    /**
     * 职称 (如：主任医师、主治医师)
     */
    private String jobTitle;

    /**
     * 医生简介
     */
    private String introduction;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}