package com.medical.smart_medical_server.VO;

import lombok.Data;

/**
 * 医生资料 VO
 */
@Data
public class DoctorProfileVO {

    private Long id;
    private String username;
    private String name;
    private Long deptId;
    private String jobTitle;
    private String introduction;
    private String phone;
}
