package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 科室查询 DTO
 */
@Data
public class DepartmentQueryDTO {

    /**
     * 科室名称（模糊搜索）
     */
    private String deptName;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}