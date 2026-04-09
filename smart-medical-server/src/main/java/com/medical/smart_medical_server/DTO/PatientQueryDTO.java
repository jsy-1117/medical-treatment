package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 患者查询 DTO（用于分页和条件查询）
 */
@Data
public class PatientQueryDTO {

    /**
     * 用户名（模糊搜索）
     */
    private String username;

    /**
     * 姓名（模糊搜索）
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}