package com.medical.smart_medical_server.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 诊疗创建 DTO
 */
@Data
public class DiagnosisCreateDTO {

    @NotNull(message = "挂号单ID不能为空")
    private Long appointmentId;

    private String symptom;

    private String diagnosisResult;

    private String prescription;
}