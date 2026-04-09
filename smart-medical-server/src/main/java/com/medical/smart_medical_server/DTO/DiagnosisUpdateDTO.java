package com.medical.smart_medical_server.DTO;

import lombok.Data;

/**
 * 诊疗更新 DTO
 */
@Data
public class DiagnosisUpdateDTO {

    private String symptom;

    private String diagnosisResult;

    private String prescription;
}