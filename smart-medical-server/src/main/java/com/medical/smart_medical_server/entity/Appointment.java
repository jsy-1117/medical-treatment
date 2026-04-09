package com.medical.smart_medical_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 挂号记录实体类
 */
@Data
@TableName("appointment")
public class Appointment {

    /**
     * 挂号单ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 排班ID
     */
    private Long scheduleId;

    /**
     * 状态 0:待就诊 1:已完成 2:已取消
     */
    private Integer status;

    /**
     * 挂号时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}