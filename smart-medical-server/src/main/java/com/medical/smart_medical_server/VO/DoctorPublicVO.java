package com.medical.smart_medical_server.VO;

import lombok.Data;

/**
 * 医生公开信息 VO（用户端使用，不包含敏感信息）
 */
@Data
public class DoctorPublicVO {

    private Long id;

    /**
     * 所属科室ID
     */
    private Long deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 医生姓名
     */
    private String name;

    /**
     * 职称
     */
    private String jobTitle;

    /**
     * 医生简介
     */
    private String introduction;

    // 不包含 username、password、phone 等敏感信息
}