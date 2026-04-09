package com.medical.smart_medical_server.DTO;

import lombok.Data;
import jakarta.validation.constraints.Size;

/**
 * 科室更新 DTO
 */
@Data
public class DepartmentUpdateDTO {

    @Size(max = 50, message = "科室名称长度不能超过50个字符")
    private String deptName;

    @Size(max = 255, message = "科室描述长度不能超过255个字符")
    private String deptDesc;

    @Size(max = 100, message = "科室位置长度不能超过100个字符")
    private String location;
}