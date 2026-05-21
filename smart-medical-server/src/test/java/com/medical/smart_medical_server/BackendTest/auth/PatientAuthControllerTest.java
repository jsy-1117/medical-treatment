package com.medical.smart_medical_server.BackendTest.auth;

import com.medical.smart_medical_server.DTO.PatientLoginDTO;
import com.medical.smart_medical_server.DTO.PatientRegisterDTO;
import com.medical.smart_medical_server.VO.PatientLoginVO;
import com.medical.smart_medical_server.VO.PatientProfileVO;
import com.medical.smart_medical_server.VO.PatientRegisterVO;
import com.medical.smart_medical_server.service.PatientAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("患者认证接口测试")
class PatientAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private PatientAuthService patientAuthService;

    @Test
    @DisplayName("患者注册成功 → 返回 200 和用户信息")
    void testRegister_Success() throws Exception {
        PatientRegisterVO mockVO = new PatientRegisterVO();
        mockVO.setId(1L);
        mockVO.setUsername("testuser");
        when(patientAuthService.register(any(PatientRegisterDTO.class))).thenReturn(mockVO);

        PatientRegisterDTO dto = new PatientRegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setName("测试用户");

        mockMvc.perform(post("/api/patient/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("患者登录成功 → 返回 token")
    void testLogin_Success() throws Exception {
        PatientLoginVO mockVO = new PatientLoginVO();
        mockVO.setToken("mock-jwt-token");
        when(patientAuthService.login(any(PatientLoginDTO.class))).thenReturn(mockVO);

        PatientLoginDTO dto = new PatientLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");

        mockMvc.perform(post("/api/patient/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"));
    }

    @Test
    @DisplayName("患者登录 — 密码错误 → 返回 500")
    void testLogin_WrongPassword() throws Exception {
        when(patientAuthService.login(any(PatientLoginDTO.class)))
                .thenThrow(new com.medical.smart_medical_server.exception.BusinessException("用户名或密码错误"));

        PatientLoginDTO dto = new PatientLoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpass");

        mockMvc.perform(post("/api/patient/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("获取用户信息 → 返回用户资料")
    void testGetProfile() throws Exception {
        PatientProfileVO mockVO = new PatientProfileVO();
        mockVO.setId(1L);
        mockVO.setUsername("testuser");
        mockVO.setName("测试用户");
        when(patientAuthService.getProfile(1L)).thenReturn(mockVO);

        mockMvc.perform(get("/api/patient/profile")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
}
