package com.medical.smart_medical_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医生排班实体类
 */
@Data
@TableName("doctor_schedule")
public class DoctorSchedule {

    /**
     * 排班ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 科室ID (冗余字段方便查询)
     */
    private Long deptId;

    /**
     * 出诊日期
     */
    private LocalDate workDate;

    /**
     * 时段 1:上午 2:下午
     */
    private Integer shiftType;

    /**
     * 总号源数量
     */
    private Integer quota;

    /**
     * 剩余号源
     */
    private Integer remainingQuota;

    /**
     * 状态 1:正常 0:停诊
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}