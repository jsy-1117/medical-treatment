package com.medical.smart_medical_server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medical.smart_medical_server.DTO.DoctorCreateDTO;
import com.medical.smart_medical_server.DTO.DoctorQueryDTO;
import com.medical.smart_medical_server.DTO.DoctorUpdateDTO;
import com.medical.smart_medical_server.entity.Doctor;
import com.medical.smart_medical_server.VO.DoctorVO;

/**
 * 医生服务接口
 */
public interface DoctorService extends IService<Doctor> {

    IPage<DoctorVO> queryDoctorPage(DoctorQueryDTO queryDTO);

    DoctorVO getDoctorById(Long id);

    DoctorVO createDoctor(DoctorCreateDTO createDTO);

    DoctorVO updateDoctor(Long id, DoctorUpdateDTO updateDTO);

    void deleteDoctor(Long id);

    Doctor getByUsername(String username);
}