package com.medical.smart_medical_server.BackendTest.appointment;

import com.medical.smart_medical_server.DTO.AppointmentCreateDTO;
import com.medical.smart_medical_server.VO.AppointmentVO;
import com.medical.smart_medical_server.service.AppointmentService;
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
@DisplayName("患者挂号接口测试")
class PatientAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    @DisplayName("创建挂号 → 返回挂号信息")
    void testCreateAppointment() throws Exception {
        AppointmentVO mockVO = new AppointmentVO();
        mockVO.setId(1L);
        mockVO.setPatientId(1L);
        mockVO.setStatus(0);
        when(appointmentService.createAppointment(any(AppointmentCreateDTO.class))).thenReturn(mockVO);

        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setScheduleId(1L);

        mockMvc.perform(post("/api/patient/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("查询我的挂号记录 → 返回分页数据")
    void testMyAppointments() throws Exception {
        AppointmentVO appointment = new AppointmentVO();
        appointment.setId(1L);
        appointment.setPatientId(1L);
        appointment.setDoctorName("张医生");
        com.baomidou.mybatisplus.core.metadata.IPage<AppointmentVO> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(java.util.List.of(appointment));
        page.setTotal(1);
        when(appointmentService.queryPatientAppointments(1L, 1, 10)).thenReturn(page);

        mockMvc.perform(get("/api/patient/appointment/my/1")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].doctorName").value("张医生"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("取消挂号 → 成功")
    void testCancelAppointment() throws Exception {
        mockMvc.perform(put("/api/patient/appointment/1/cancel")
                        .param("patientId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
