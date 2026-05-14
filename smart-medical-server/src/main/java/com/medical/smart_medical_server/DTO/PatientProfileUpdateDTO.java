package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 患者资料更新 DTO
 */
@Data
public class PatientProfileUpdateDTO {

    private String name;
    private String phone;
    private Integer gender;
    private Integer age;
    private String idCard;
}
