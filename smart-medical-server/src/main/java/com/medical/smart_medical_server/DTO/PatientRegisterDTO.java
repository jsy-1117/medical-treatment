package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 患者注册 DTO
 */
@Data
public class PatientRegisterDTO {

    private String username;
    private String password;
    private String name;
    private String phone;
}
