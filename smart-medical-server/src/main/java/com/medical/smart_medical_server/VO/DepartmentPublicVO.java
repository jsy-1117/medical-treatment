package com.medical.smart_medical_server.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 科室公开信息 VO（用于 LLM 和用户端展示）
 * 安全性：不包含 id 等内部字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPublicVO {

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 科室描述（诊疗范围）
     */
    private String deptDesc;

    /**
     * 科室位置
     */
    private String location;
}