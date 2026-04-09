package com.medical.smart_medical_server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medical.smart_medical_server.DTO.PatientCreateDTO;
import com.medical.smart_medical_server.DTO.PatientQueryDTO;
import com.medical.smart_medical_server.DTO.PatientUpdateDTO;
import com.medical.smart_medical_server.common.Result;
import com.medical.smart_medical_server.service.PatientService;
import com.medical.smart_medical_server.VO.PatientVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 患者管理控制器
 */
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 分页查询患者列表
     */
    @GetMapping("/list")
    public Result<IPage<PatientVO>> list(PatientQueryDTO queryDTO) {
        IPage<PatientVO> page = patientService.queryPatientPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据ID获取患者详情
     */
    @GetMapping("/{id}")
    public Result<PatientVO> getById(@PathVariable Long id) {
        PatientVO patientVO = patientService.getPatientById(id);
        return Result.success(patientVO);
    }

    /**
     * 创建患者
     */
    @PostMapping
    public Result<PatientVO> create(@Validated @RequestBody PatientCreateDTO createDTO) {
        PatientVO patientVO = patientService.createPatient(createDTO);
        return Result.success(patientVO);
    }

    /**
     * 更新患者信息
     */
    @PutMapping("/{id}")
    public Result<PatientVO> update(
            @PathVariable Long id,
            @Validated @RequestBody PatientUpdateDTO updateDTO) {
        PatientVO patientVO = patientService.updatePatient(id, updateDTO);
        return Result.success(patientVO);
    }

    /**
     * 删除患者
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Result.success();
    }
}