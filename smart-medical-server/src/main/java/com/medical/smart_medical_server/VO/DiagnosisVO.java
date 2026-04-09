package com.medical.smart_medical_server.VO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 诊疗信息 VO
 */
@Data
public class DiagnosisVO {

    private Long id;

    private Long appointmentId;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;

    private String symptom;

    private String diagnosisResult;

    private String prescription;

    private LocalDateTime createTime;

    // 挂号相关信息
    private String workDate;

    private Integer shiftType;
}