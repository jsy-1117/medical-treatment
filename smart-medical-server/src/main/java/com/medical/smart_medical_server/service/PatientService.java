package com.medical.smart_medical_server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.smart_medical_server.DTO.PatientCreateDTO;
import com.medical.smart_medical_server.DTO.PatientQueryDTO;
import com.medical.smart_medical_server.DTO.PatientUpdateDTO;
import com.medical.smart_medical_server.entity.Patient;
import com.medical.smart_medical_server.VO.PatientVO;

/**
 * 患者服务接口
 */
public interface PatientService extends IService<Patient> {

    /**
     * 分页查询患者列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<PatientVO> queryPatientPage(PatientQueryDTO queryDTO);

    /**
     * 根据ID查询患者详情
     * @param id 患者ID
     * @return 患者信息
     */
    PatientVO getPatientById(Long id);

    /**
     * 创建患者
     * @param createDTO 创建信息
     * @return 患者信息
     */
    PatientVO createPatient(PatientCreateDTO createDTO);

    /**
     * 更新患者信息
     * @param id 患者ID
     * @param updateDTO 更新信息
     * @return 更新后的患者信息
     */
    PatientVO updatePatient(Long id, PatientUpdateDTO updateDTO);

    /**
     * 删除患者
     * @param id 患者ID
     */
    void deletePatient(Long id);

    /**
     * 根据用户名查询患者
     * @param username 用户名
     * @return 患者信息
     */
    Patient getByUsername(String username);
}