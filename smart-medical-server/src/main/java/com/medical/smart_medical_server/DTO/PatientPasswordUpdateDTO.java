package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 患者修改密码 DTO
 */
@Data
public class PatientPasswordUpdateDTO {

    private String oldPassword;
    private String newPassword;
}
