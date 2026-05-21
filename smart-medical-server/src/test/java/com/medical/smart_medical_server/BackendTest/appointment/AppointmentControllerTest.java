package com.medical.smart_medical_server.BackendTest.appointment;

import com.medical.smart_medical_server.DTO.AppointmentQueryDTO;
import com.medical.smart_medical_server.VO.AppointmentVO;
import com.medical.smart_medical_server.service.AppointmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@DisplayName("挂号管理接口测试")
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    @DisplayName("分页查询挂号记录 → 返回分页数据")
    void testListAppointments() throws Exception {
        AppointmentVO appointment = new AppointmentVO();
        appointment.setId(1L);
        appointment.setPatientName("测试患者");
        appointment.setDoctorName("张医生");
        com.baomidou.mybatisplus.core.metadata.IPage<AppointmentVO> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(java.util.List.of(appointment));
        page.setTotal(1);
        when(appointmentService.queryAppointmentPage(any(AppointmentQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/appointment/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].doctorName").value("张医生"))
                .andExpect(jsonPath("$.data.records[0].patientName").value("测试患者"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("更新挂号状态 → 成功")
    void testUpdateAppointmentStatus() throws Exception {
        mockMvc.perform(put("/api/appointment/1/status")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("取消挂号 → 成功")
    void testCancelAppointment() throws Exception {
        mockMvc.perform(put("/api/appointment/1/cancel")
                        .param("patientId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
