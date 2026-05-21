package com.medical.smart_medical_server.BackendTest.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.ScheduleQueryDTO;
import com.medical.smart_medical_server.VO.DoctorScheduleVO;
import com.medical.smart_medical_server.entity.DoctorSchedule;
import com.medical.smart_medical_server.service.impl.DoctorScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 排班动态排序单元测试。
 * 从原 DoctorScheduleServiceImplTest 迁移，验证动态排序逻辑的正确性。
 * 使用 @SpringBootTest 以满足 MyBatis-Plus Lambda 缓存初始化需求。
 * 由于 applyDynamicSort 为包级私有方法，通过反射调用。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("排班动态排序测试")
class ScheduleDynamicSortTest {

    @Autowired
    private DoctorScheduleServiceImpl scheduleService;

    private LambdaQueryWrapper<DoctorSchedule> wrapper;
    private Method applyDynamicSort;

    @BeforeEach
    void setUp() throws Exception {
        wrapper = new LambdaQueryWrapper<>();
        applyDynamicSort = DoctorScheduleServiceImpl.class.getDeclaredMethod(
                "applyDynamicSort", LambdaQueryWrapper.class, ScheduleQueryDTO.class);
        applyDynamicSort.setAccessible(true);
    }

    private void invokeSort(ScheduleQueryDTO dto) throws Exception {
        applyDynamicSort.invoke(scheduleService, wrapper, dto);
    }

    @Test
    @DisplayName("默认排序 — 未传入 sortField → workDate ASC, shiftType ASC")
    void shouldUseDefaultSortWhenSortFieldIsNull() throws Exception {
        invokeSort(new ScheduleQueryDTO());
        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("shift_type ASC");
    }

    @Test
    @DisplayName("默认排序 — sortField 为空字符串 → 回退默认排序")
    void shouldUseDefaultSortWhenSortFieldIsBlank() throws Exception {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("   ");
        invokeSort(dto);
        assertThat(wrapper.getSqlSegment()).contains("work_date ASC");
    }

    @Test
    @DisplayName("单字段升序 → workDate ASC")
    void shouldSortSingleFieldAscending() throws Exception {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate");
        dto.setSortDirection("asc");
        invokeSort(dto);
        assertThat(wrapper.getSqlSegment()).contains("work_date ASC");
    }

    @Test
    @DisplayName("单字段降序 → workDate DESC")
    void shouldSortSingleFieldDescending() throws Exception {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate");
        dto.setSortDirection("desc");
        invokeSort(dto);
        assertThat(wrapper.getSqlSegment()).contains("work_date DESC");
    }

    @Test
    @DisplayName("多字段混合排序 → workDate ASC, shiftType DESC")
    void shouldSortMultipleFieldsWithMixedDirections() throws Exception {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate,shiftType");
        dto.setSortDirection("asc,desc");
        invokeSort(dto);
        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("shift_type DESC");
    }

    @Test
    @DisplayName("方向数少于字段数 → 缺失方向默认 ASC")
    void shouldDefaultRemainingDirectionsToAsc() throws Exception {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate,quota");
        dto.setSortDirection("asc");
        invokeSort(dto);
        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("quota ASC");
    }

    @Test
    @DisplayName("白名单外字段 → 静默忽略，回退默认排序")
    void shouldIgnoreNonWhitelistFields() throws Exception {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("injection;DROP TABLE");
        dto.setSortDirection("asc");
        invokeSort(dto);
        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("shift_type ASC");
        assertThat(sql).doesNotContain("DROP TABLE");
    }

    @Test
    @DisplayName("未传入 sortDirection → 默认 ASC")
    void shouldHandleEmptySortDirection() throws Exception {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("quota");
        invokeSort(dto);
        assertThat(wrapper.getSqlSegment()).contains("quota ASC");
    }
}
