package com.medical.smart_medical_server.DTO;

import lombok.Data;
import java.time.LocalDate;

/**
 * 挂号查询 DTO
 */
@Data
public class AppointmentQueryDTO {

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 状态 0:待就诊 1:已完成 2:已取消
     */
    private Integer status;

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