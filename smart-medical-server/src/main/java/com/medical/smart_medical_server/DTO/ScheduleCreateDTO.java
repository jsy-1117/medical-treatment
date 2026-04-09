package com.medical.smart_medical_server.DTO;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 排班创建 DTO
 */
@Data
public class ScheduleCreateDTO {

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "科室ID不能为空")
    private Long deptId;

    @NotNull(message = "出诊日期不能为空")
    private LocalDate workDate;

    @NotNull(message = "时段不能为空")
    private Integer shiftType;

    @NotNull(message = "号源数量不能为空")
    @Min(value = 1, message = "号源数量必须大于0")
    private Integer quota;
}