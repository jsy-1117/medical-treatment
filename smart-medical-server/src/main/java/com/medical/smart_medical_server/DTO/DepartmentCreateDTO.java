package com.medical.smart_medical_server.DTO;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 科室创建 DTO
 */
@Data
public class DepartmentCreateDTO {

    @NotBlank(message = "科室名称不能为空")
    @Size(max = 50, message = "科室名称长度不能超过50个字符")
    private String deptName;

    @Size(max = 255, message = "科室描述长度不能超过255个字符")
    private String deptDesc;

    @Size(max = 100, message = "科室位置长度不能超过100个字符")
    private String location;
}