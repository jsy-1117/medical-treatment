package com.medical.smart_medical_server.BackendTest.schedule;

import com.medical.smart_medical_server.DTO.ScheduleCreateDTO;
import com.medical.smart_medical_server.DTO.ScheduleQueryDTO;
import com.medical.smart_medical_server.VO.DoctorScheduleVO;
import com.medical.smart_medical_server.service.DoctorScheduleService;
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
@DisplayName("排班管理接口测试")
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private DoctorScheduleService scheduleService;

    @Test
    @DisplayName("分页查询排班列表 → 返回分页数据")
    void testListSchedules() throws Exception {
        DoctorScheduleVO schedule = new DoctorScheduleVO();
        schedule.setId(1L);
        schedule.setDoctorName("张医生");
        com.baomidou.mybatisplus.core.metadata.IPage<DoctorScheduleVO> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(java.util.List.of(schedule));
        page.setTotal(1);
        when(scheduleService.querySchedulePage(any(ScheduleQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/schedule/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("sortField", "workDate,shiftType")
                        .param("sortDirection", "asc,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].doctorName").value("张医生"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("创建排班 → 返回新排班信息")
    void testCreateSchedule() throws Exception {
        DoctorScheduleVO mockVO = new DoctorScheduleVO();
        mockVO.setId(1L);
        mockVO.setDoctorId(1L);
        mockVO.setWorkDate(java.time.LocalDate.now());
        mockVO.setShiftType(1);
        mockVO.setQuota(20);
        when(scheduleService.createSchedule(any(ScheduleCreateDTO.class))).thenReturn(mockVO);

        ScheduleCreateDTO dto = new ScheduleCreateDTO();
        dto.setDoctorId(1L);
        dto.setDeptId(1L);
        dto.setWorkDate(java.time.LocalDate.now());
        dto.setShiftType(1);
        dto.setQuota(20);

        mockMvc.perform(post("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.quota").value(20));
    }

    @Test
    @DisplayName("更新排班状态 → 成功")
    void testUpdateScheduleStatus() throws Exception {
        mockMvc.perform(put("/api/schedule/1/status")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除排班 → 成功")
    void testDeleteSchedule() throws Exception {
        mockMvc.perform(delete("/api/schedule/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
