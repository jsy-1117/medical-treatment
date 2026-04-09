package com.medical.smart_medical_server.VO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 科室 VO
 */
@Data
public class DepartmentVO {

    private Long id;
    private String deptName;
    private String deptDesc;
    private String location;
    private LocalDateTime createTime;
}