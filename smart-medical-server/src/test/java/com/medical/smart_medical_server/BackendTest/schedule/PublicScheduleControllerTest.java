package com.medical.smart_medical_server.BackendTest.schedule;

import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.entity.DoctorSchedule;
import com.medical.smart_medical_server.service.DoctorScheduleService;
import com.medical.smart_medical_server.service.DoctorService;
import com.medical.smart_medical_server.service.DepartmentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("公开排班接口测试")
class PublicScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorScheduleService scheduleService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private DepartmentService departmentService;

    @Test
    @DisplayName("查询可挂号排班列表 → 返回排班分页数据")
    void testGetAvailableSchedules() throws Exception {
        com.baomidou.mybatisplus.core.metadata.IPage<DoctorSchedule> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setTotal(0);
        when(scheduleService.page(any(com.baomidou.mybatisplus.core.metadata.IPage.class),
                any(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/public/schedule/available")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("获取排班详情 → 返回排班信息")
    void testGetScheduleById() throws Exception {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setId(1L);
        schedule.setDoctorId(1L);
        schedule.setDeptId(1L);
        schedule.setWorkDate(java.time.LocalDate.now());
        schedule.setShiftType(1);
        schedule.setRemainingQuota(10);
        when(scheduleService.getById(1L)).thenReturn(schedule);
        when(doctorService.getById(1L)).thenReturn(new Doctor());

        mockMvc.perform(get("/api/public/schedule/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("获取排班详情 — 不存在 → 返回错误")
    void testGetScheduleById_NotFound() throws Exception {
        when(scheduleService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/public/schedule/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
