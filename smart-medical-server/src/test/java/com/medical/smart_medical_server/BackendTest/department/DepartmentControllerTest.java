package com.medical.smart_medical_server.BackendTest.department;

import com.medical.smart_medical_server.DTO.DepartmentCreateDTO;
import com.medical.smart_medical_server.DTO.DepartmentQueryDTO;
import com.medical.smart_medical_server.VO.DepartmentVO;
import com.medical.smart_medical_server.service.DepartmentService;
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
@DisplayName("科室管理接口测试")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    @Test
    @DisplayName("分页查询科室列表 → 返回分页数据")
    void testListDepartments() throws Exception {
        com.baomidou.mybatisplus.core.metadata.IPage<DepartmentVO> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        DepartmentVO dept = new DepartmentVO();
        dept.setId(1L);
        dept.setDeptName("内科");
        page.setRecords(java.util.List.of(dept));
        page.setTotal(1);
        when(departmentService.queryDepartmentPage(any(DepartmentQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/department/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].deptName").value("内科"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("创建科室 → 返回新科室信息")
    void testCreateDepartment() throws Exception {
        DepartmentVO mockVO = new DepartmentVO();
        mockVO.setId(1L);
        mockVO.setDeptName("儿科");
        when(departmentService.createDepartment(any(DepartmentCreateDTO.class))).thenReturn(mockVO);

        DepartmentCreateDTO dto = new DepartmentCreateDTO();
        dto.setDeptName("儿科");

        mockMvc.perform(post("/api/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deptName").value("儿科"));
    }
}
