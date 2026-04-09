package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 医生查询 DTO
 */
@Data
public class DoctorQueryDTO {

    /**
     * 姓名（模糊搜索）
     */
    private String name;

    /**
     * 科室ID
     */
    private Long deptId;

    /**
     * 职称
     */
    private String jobTitle;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}