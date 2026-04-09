package com.medical.smart_medical_server.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 排班信息 VO（用于 LLM 上下文）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ 添加这行：忽略未知字段
public class ScheduleLLMVO {

    private String doctorName;
    private String doctorJobTitle;
    private String deptName;
    private LocalDate workDate;
    private Integer shiftType;
    private Integer remainingQuota;
    private Integer status;

    // ========== 便捷方法（不序列化）==========

    @JsonIgnore  // ✅ 添加：不序列化
    public String getShiftTypeDesc() {
        if (shiftType == null) return "未知";
        return shiftType == 1 ? "上午" : "下午";
    }

    @JsonIgnore  // ✅ 添加
    public String getStatusDesc() {
        if (status == null) return "未知";
        return status == 1 ? "正常" : "停诊";
    }

    @JsonIgnore  // ✅ 添加
    public boolean isAvailable() {
        return status != null && status == 1
                && remainingQuota != null && remainingQuota > 0;
    }

    @JsonIgnore  // ✅ 添加
    public String getQuotaDesc() {
        if (!isAvailable()) {
            return "已约满/停诊";
        }
        if (remainingQuota <= 5) {
            return "余号 " + remainingQuota + "（紧张）";
        }
        return "余号 " + remainingQuota;
    }

    @JsonIgnore  // ✅ 添加
    public String getFormattedDate() {
        if (workDate == null) return "未知";
        return workDate.format(DateTimeFormatter.ofPattern("M月d日"));
    }

    @JsonIgnore  // ✅ 添加
    public String toLLMText() {
        return String.format("%s %s %s（%s，%s科）%s",
                getFormattedDate(),
                getShiftTypeDesc(),
                doctorName != null ? doctorName : "医生",
                doctorJobTitle != null ? doctorJobTitle : "医师",
                deptName != null ? deptName : "综合",
                getQuotaDesc()
        );
    }
}