package com.medical.smart_medical_server.BackendTest.doctor;

import com.medical.smart_medical_server.VO.DoctorPublicVO;
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
@DisplayName("公开医生接口测试")
class PublicDoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private DepartmentService departmentService;

    @Test
    @DisplayName("分页查询医生列表 → 返回医生分页数据")
    void testListDoctors() throws Exception {
        com.baomidou.mybatisplus.core.metadata.IPage<com.medical.smart_medical_server.entity.Doctor> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        com.medical.smart_medical_server.entity.Doctor doctor = new com.medical.smart_medical_server.entity.Doctor();
        doctor.setId(1L);
        doctor.setName("张医生");
        page.setRecords(java.util.List.of(doctor));
        page.setTotal(1);

        when(doctorService.page(any(com.baomidou.mybatisplus.core.metadata.IPage.class),
                any(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/public/doctor/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("根据 ID 获取医生详情 → 返回医生信息")
    void testGetDoctorById() throws Exception {
        com.medical.smart_medical_server.entity.Doctor doctor = new com.medical.smart_medical_server.entity.Doctor();
        doctor.setId(1L);
        doctor.setName("张医生");
        doctor.setJobTitle("主任医师");
        doctor.setDeptId(1L);
        when(doctorService.getById(1L)).thenReturn(doctor);
        when(departmentService.getDepartmentById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/public/doctor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("张医生"));
    }
}
