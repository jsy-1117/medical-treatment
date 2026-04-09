package com.medical.smart_medical_server.VO;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员信息 VO（不包含密码）
 */
@Data
public class AdminVO {

    private Long id;
    private String username;
    private String name;
    private LocalDateTime createTime;
}