package com.medical.smart_medical_server.VO;

import lombok.Data;
import java.time.LocalDate;

/**
 * 排班公开信息 VO（用户端使用，用于挂号选择）
 */
@Data
public class SchedulePublicVO {

    private Long id;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 医生职称
     */
    private String doctorJobTitle;

    /**
     * 科室ID
     */
    private Long deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 出诊日期
     */
    private LocalDate workDate;

    /**
     * 时段 1:上午 2:下午
     */
    private Integer shiftType;

    /**
     * 剩余号源
     */
    private Integer remainingQuota;

    /**
     * 状态 1:正常 0:停诊
     */
    private Integer status;

    /**
     * 时段描述
     */
    public String getShiftTypeDesc() {
        if (shiftType == null) return "未知";
        return shiftType == 1 ? "上午" : "下午";
    }

    /**
     * 是否可挂号
     */
    public Boolean getCanBook() {
        return status != null && status == 1 && remainingQuota != null && remainingQuota > 0;
    }
}