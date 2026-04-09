package com.medical.smart_medical_server.VO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 医生信息 VO（不包含密码）
 */
@Data
public class DoctorVO {

    private Long id;
    private Long deptId;
    private String deptName;  // 科室名称（关联查询）
    private String username;
    private String name;
    private String jobTitle;
    private String introduction;
    private String phone;
    private LocalDateTime createTime;
}