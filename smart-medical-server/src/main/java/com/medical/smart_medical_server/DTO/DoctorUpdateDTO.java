package com.medical.smart_medical_server.DTO;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * 医生更新 DTO
 */
@Data
public class DoctorUpdateDTO {

    private Long deptId;

    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @Size(max = 50, message = "职称长度不能超过50个字符")
    private String jobTitle;

    private String introduction;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}