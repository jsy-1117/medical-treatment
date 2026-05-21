package com.medical.smart_medical_server.BackendTest.auth;

import com.medical.smart_medical_server.DTO.AdminLoginDTO;
import com.medical.smart_medical_server.VO.AdminLoginVO;
import com.medical.smart_medical_server.service.AdminService;
import com.medical.smart_medical_server.mapper.AdminMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("管理员认证接口测试")
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @MockBean
    private AdminMapper adminMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("管理员登录成功 → 返回 token")
    void testLogin_Success() throws Exception {
        AdminLoginVO mockVO = new AdminLoginVO();
        mockVO.setToken("admin-jwt-token");
        when(adminService.login(any(AdminLoginDTO.class))).thenReturn(mockVO);

        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("admin-jwt-token"));
    }

    @Test
    @DisplayName("管理员登录 — 密码错误 → 返回 500")
    void testLogin_WrongPassword() throws Exception {
        when(adminService.login(any(AdminLoginDTO.class)))
                .thenThrow(new com.medical.smart_medical_server.exception.BusinessException("用户名或密码错误"));

        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrongpass");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
