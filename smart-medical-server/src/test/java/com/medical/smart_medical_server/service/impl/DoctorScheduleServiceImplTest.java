package com.medical.smart_medical_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.ScheduleQueryDTO;
import com.medical.smart_medical_server.VO.DoctorScheduleVO;
import com.medical.smart_medical_server.entity.DoctorSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
class DoctorScheduleServiceImplTest {

    @TestConfiguration
    @EnableCaching
    static class TestCacheConfig {
        @Bean
        @Primary
        RedisConnectionFactory redisConnectionFactory() {
            return mock(RedisConnectionFactory.class);
        }

        @Bean
        @Primary
        CacheManager testCacheManager() {
            return new ConcurrentMapCacheManager("department", "doctor", "doctorList", "schedule", "llmContext");
        }
    }

    @Autowired
    private DoctorScheduleServiceImpl scheduleService;

    private LambdaQueryWrapper<DoctorSchedule> wrapper;

    @BeforeEach
    void setUp() {
        wrapper = new LambdaQueryWrapper<>();
    }

    @Test
    void shouldUseDefaultSortWhenSortFieldIsNull() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("shift_type ASC");
    }

    @Test
    void shouldUseDefaultSortWhenSortFieldIsBlank() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("   ");

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
    }

    @Test
    void shouldSortSingleFieldAscending() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate");
        dto.setSortDirection("asc");

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
    }

    @Test
    void shouldSortSingleFieldDescending() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate");
        dto.setSortDirection("desc");

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date DESC");
    }

    @Test
    void shouldSortMultipleFieldsWithMixedDirections() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate,shiftType");
        dto.setSortDirection("asc,desc");

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("shift_type DESC");
    }

    @Test
    void shouldDefaultRemainingDirectionsToAsc() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("workDate,quota");
        dto.setSortDirection("asc");

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("quota ASC");
    }

    @Test
    void shouldIgnoreNonWhitelistFields() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("injection;DROP TABLE");
        dto.setSortDirection("asc");

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("work_date ASC");
        assertThat(sql).contains("shift_type ASC");
        assertThat(sql).doesNotContain("DROP TABLE");
    }

    @Test
    void shouldHandleEmptySortDirection() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setSortField("quota");

        scheduleService.applyDynamicSort(wrapper, dto);

        String sql = wrapper.getSqlSegment();
        assertThat(sql).contains("quota ASC");
    }

    @Test
    void shouldReturnPaginatedResultsWithDefaultSort() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        IPage<DoctorScheduleVO> result = scheduleService.querySchedulePage(dto);

        assertThat(result).isNotNull();
        assertThat(result.getRecords()).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    void shouldReturnPaginatedResultsWithCustomSort() {
        ScheduleQueryDTO dto = new ScheduleQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setSortField("workDate");
        dto.setSortDirection("asc");

        IPage<DoctorScheduleVO> result = scheduleService.querySchedulePage(dto);

        assertThat(result).isNotNull();
        assertThat(result.getRecords()).isNotNull();
    }
}
