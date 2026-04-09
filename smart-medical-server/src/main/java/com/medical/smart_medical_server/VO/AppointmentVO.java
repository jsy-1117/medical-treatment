package com.medical.smart_medical_server.VO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录 VO
 */
@Data
public class AppointmentVO {

    private Long id;
    private Long patientId;
    private String patientName;     // 患者姓名（关联查询）
    private Long doctorId;
    private String doctorName;      // 医生姓名（关联查询）
    private Long scheduleId;
    private LocalDate workDate;     // 就诊日期（关联查询）
    private Integer shiftType;      // 时段（关联查询）
    private String deptName;        // 科室名称（关联查询）
    private Integer status;
    private LocalDateTime createTime;

    /**
     * 状态描述
     */
    public String getStatusDesc() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待就诊";
            case 1: return "已完成";
            case 2: return "已取消";
            default: return "未知";
        }
    }

    /**
     * 时段描述
     */
    public String getShiftTypeDesc() {
        if (shiftType == null) return "未知";
        return shiftType == 1 ? "上午" : "下午";
    }
}