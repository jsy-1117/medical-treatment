package com.medical.smart_medical_server.BackendTest.department;

import com.medical.smart_medical_server.VO.DepartmentVO;
import com.medical.smart_medical_server.service.DepartmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("公开科室接口测试")
class PublicDepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Test
    @DisplayName("获取所有科室列表 → 返回科室列表")
    void testGetAllDepartments() throws Exception {
        DepartmentVO dept1 = new DepartmentVO();
        dept1.setId(1L);
        dept1.setDeptName("内科");
        DepartmentVO dept2 = new DepartmentVO();
        dept2.setId(2L);
        dept2.setDeptName("外科");
        when(departmentService.getAllDepartments()).thenReturn(List.of(dept1, dept2));

        mockMvc.perform(get("/api/public/department/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].deptName").value("内科"));
    }

    @Test
    @DisplayName("根据 ID 获取科室详情 → 返回科室信息")
    void testGetDepartmentById() throws Exception {
        DepartmentVO dept = new DepartmentVO();
        dept.setId(1L);
        dept.setDeptName("内科");
        dept.setDeptDesc("内科疾病诊治");
        when(departmentService.getDepartmentById(1L)).thenReturn(dept);

        mockMvc.perform(get("/api/public/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deptName").value("内科"));
    }
}
