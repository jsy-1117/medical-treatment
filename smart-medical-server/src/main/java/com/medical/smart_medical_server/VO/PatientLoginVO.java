package com.medical.smart_medical_server.VO;

import lombok.Data;

/**
 * 患者登录 VO
 */
@Data
public class PatientLoginVO {

    private String token;
    private Long id;
    private String username;
    private String name;
    private String phone;
    private Integer gender;
    private Integer age;
    private String idCard;
}
