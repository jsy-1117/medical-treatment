package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 医生修改密码 DTO
 */
@Data
public class DoctorPasswordUpdateDTO {

    private String oldPassword;
    private String newPassword;
}
