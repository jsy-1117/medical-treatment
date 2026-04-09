package com.medical.smart_medical_server.DTO;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 挂号创建 DTO（用户端挂号使用）
 */
@Data
public class AppointmentCreateDTO {

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;
}