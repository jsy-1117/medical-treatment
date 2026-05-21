package com.medical.smart_medical_server.BackendTest.doctor;

import com.medical.smart_medical_server.DTO.DoctorCreateDTO;
import com.medical.smart_medical_server.DTO.DoctorQueryDTO;
import com.medical.smart_medical_server.VO.DoctorVO;
import com.medical.smart_medical_server.service.DoctorService;
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("医生管理接口测试")
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    @Test
    @DisplayName("分页查询医生列表 → 返回分页数据")
    void testListDoctors() throws Exception {
        DoctorVO doctor = new DoctorVO();
        doctor.setId(1L);
        doctor.setName("李医生");
        com.baomidou.mybatisplus.core.metadata.IPage<DoctorVO> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(java.util.List.of(doctor));
        page.setTotal(1);
        when(doctorService.queryDoctorPage(any(DoctorQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/doctor/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].name").value("李医生"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("创建医生 → 返回新医生信息")
    void testCreateDoctor() throws Exception {
        DoctorVO mockVO = new DoctorVO();
        mockVO.setId(1L);
        mockVO.setName("王医生");
        when(doctorService.createDoctor(any(DoctorCreateDTO.class))).thenReturn(mockVO);

        DoctorCreateDTO dto = new DoctorCreateDTO();
        dto.setName("王医生");
        dto.setUsername("wangdoctor");
        dto.setPassword("123456");
        dto.setDeptId(1L);

        mockMvc.perform(post("/api/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("王医生"));
    }
}
