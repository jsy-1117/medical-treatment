package com.medical.smart_medical_server.service;

import com.medical.smart_medical_server.DTO.PatientLoginDTO;
import com.medical.smart_medical_server.DTO.PatientPasswordUpdateDTO;
import com.medical.smart_medical_server.DTO.PatientProfileUpdateDTO;
import com.medical.smart_medical_server.DTO.PatientRegisterDTO;
import com.medical.smart_medical_server.VO.PatientLoginVO;
import com.medical.smart_medical_server.VO.PatientProfileVO;
import com.medical.smart_medical_server.VO.PatientRegisterVO;

public interface PatientAuthService {

    PatientLoginVO login(PatientLoginDTO loginDTO);

    PatientRegisterVO register(PatientRegisterDTO registerDTO);

    PatientProfileVO getProfile(Long patientId);

    PatientProfileVO updateProfile(Long patientId, PatientProfileUpdateDTO updateDTO);

    void updatePassword(Long patientId, PatientPasswordUpdateDTO updateDTO);
}
