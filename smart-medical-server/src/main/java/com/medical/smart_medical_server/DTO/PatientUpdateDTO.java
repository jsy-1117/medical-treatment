package com.medical.smart_medical_server.DTO;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

/**
 * 患者更新 DTO
 */
@Data
public class PatientUpdateDTO {

    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 性别 1:男 2:女
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 身份证号
     */
    @Size(max = 20, message = "身份证号长度不能超过20个字符")
    private String idCard;
}