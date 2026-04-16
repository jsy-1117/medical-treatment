package com.medical.smart_medical_server.VO;

import lombok.Data;

/**
 * 患者资料 VO
 */
@Data
public class PatientProfileVO {

    private Long id;
    private String username;
    private String name;
    private String phone;
    private Integer gender;
    private Integer age;
    private String idCard;
}
