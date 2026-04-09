package com.medical.smart_medical_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 诊疗信息实体
 */
@Data
@TableName("diagnosis")
public class Diagnosis {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appointmentId;

    private Long patientId;

    private Long doctorId;

    private String symptom;

    private String diagnosisResult;

    private String prescription;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}