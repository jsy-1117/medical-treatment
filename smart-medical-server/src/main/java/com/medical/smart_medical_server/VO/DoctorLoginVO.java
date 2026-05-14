package com.medical.smart_medical_server.VO;

import lombok.Data;

/**
 * 医生登录 VO
 */
@Data
public class DoctorLoginVO {

    private String token;
    private Long id;
    private String username;
    private String name;
    private Long deptId;
    private String jobTitle;
    private String phone;
}
