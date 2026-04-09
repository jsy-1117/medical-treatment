package com.medical.smart_medical_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 科室实体类
 */
@Data
@TableName("department")
public class Department {

    /**
     * 科室ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 科室名称 (如：内科、外科)
     */
    private String deptName;

    /**
     * 科室描述
     */
    private String deptDesc;

    /**
     * 科室位置
     */
    private String location;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}