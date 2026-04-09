package com.medical.smart_medical_server.VO;

import lombok.Data;

@Data
public class AdminLoginVO {

    private Long id;
    private String username;
    private String name;
    private String token;  // JWT Token
}
