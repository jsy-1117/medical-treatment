package com.medical.smart_medical_server.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医生排班 VO
 */
@Data
public class DoctorScheduleVO {

    private Long id;
    private Long doctorId;
    private String doctorName;  // 医生姓名（关联查询）
    private String doctorJobTitle;
    private Long deptId;
    private String deptName;    // 科室名称（关联查询）
    private LocalDate workDate;
    private Integer shiftType;
    private Integer quota;
    private Integer remainingQuota;
    private Integer status;
    private LocalDateTime createTime;

    /**
     * 时段描述
     */
    @JsonIgnore
    public String getShiftTypeDesc() {
        if (shiftType == null) return "未知";
        return shiftType == 1 ? "上午" : "下午";
    }

    /**
     * 状态描述
     */
    @JsonIgnore
    public String getStatusDesc() {
        if (status == null) return "未知";
        return status == 1 ? "正常" : "停诊";
    }
}