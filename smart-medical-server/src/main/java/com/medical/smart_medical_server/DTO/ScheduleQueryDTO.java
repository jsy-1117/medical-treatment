package com.medical.smart_medical_server.DTO;

import lombok.Data;
import java.time.LocalDate;

/**
 * 排班查询 DTO
 */
@Data
public class ScheduleQueryDTO {

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 科室ID
     */
    private Long deptId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段，多个字段用逗号分隔，如 "workDate,shiftType"
     * 仅白名单内字段生效（workDate / shiftType / quota / remainingQuota / createTime / status）
     */
    private String sortField;

    /**
     * 排序方向，与 sortField 一一对应，用逗号分隔，如 "asc,asc"
     * 个数少于 sortField 时后续默认 asc；不传默认 asc
     */
    private String sortDirection;
}