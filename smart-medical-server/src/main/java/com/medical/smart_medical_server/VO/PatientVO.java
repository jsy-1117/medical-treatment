package com.medical.smart_medical_server.VO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 患者信息 VO（不包含密码）
 */
@Data
public class PatientVO {

    private Long id;
    private String username;
    private String name;
    private Integer gender;
    private Integer age;
    private String phone;
    private String idCard;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 性别描述（用于前端展示）
     */
    public String getGenderDesc() {
        if (gender == null) return "未知";
        return gender == 1 ? "男" : "女";
    }
}