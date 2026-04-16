package com.medical.smart_medical_server.service;

import com.medical.smart_medical_server.DTO.DoctorLoginDTO;
import com.medical.smart_medical_server.DTO.DoctorPasswordUpdateDTO;
import com.medical.smart_medical_server.VO.DoctorLoginVO;
import com.medical.smart_medical_server.VO.DoctorProfileVO;

public interface DoctorAuthService {

    DoctorLoginVO login(DoctorLoginDTO loginDTO);

    DoctorProfileVO getProfile(String authHeader);

    void updatePassword(String authHeader, DoctorPasswordUpdateDTO updateDTO);
}
