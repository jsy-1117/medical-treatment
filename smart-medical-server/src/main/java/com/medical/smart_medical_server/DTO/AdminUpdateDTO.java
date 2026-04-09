package com.medical.smart_medical_server.DTO;

import lombok.Data;
import jakarta.validation.constraints.Size;


/**
 * 管理员更新 DTO
 */
@Data
public class AdminUpdateDTO {

    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
}