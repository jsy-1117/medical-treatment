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
}