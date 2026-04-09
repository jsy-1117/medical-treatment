package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.DoctorCreateDTO;
import com.medical.smart_medical_server.DTO.DoctorQueryDTO;
import com.medical.smart_medical_server.DTO.DoctorUpdateDTO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.DoctorService;
import com.medical.smart_medical_server.VO.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生管理控制器
 */
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/list")
    public Result<IPage<DoctorVO>> list(DoctorQueryDTO queryDTO) {
        IPage<DoctorVO> page = doctorService.queryDoctorPage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<DoctorVO> getById(@PathVariable Long id) {
        DoctorVO doctorVO = doctorService.getDoctorById(id);
        return Result.success(doctorVO);
    }

    @PostMapping
    public Result<DoctorVO> create(@Validated @RequestBody DoctorCreateDTO createDTO) {
        DoctorVO doctorVO = doctorService.createDoctor(createDTO);
        return Result.success(doctorVO);
    }

    @PutMapping("/{id}")
    public Result<DoctorVO> update(
            @PathVariable Long id,
            @Validated @RequestBody DoctorUpdateDTO updateDTO) {
        DoctorVO doctorVO = doctorService.updateDoctor(id, updateDTO);
        return Result.success(doctorVO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return Result.success();
    }
}