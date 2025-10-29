package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.entity.AdminMaster;
import com.example.wecureit_be.request.LoginRequest;
import com.example.wecureit_be.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class CommonControllerImpl {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @Autowired
    PatientControllerImpl patientControllerImpl;

    @Autowired
    AdminControllerImpl adminControllerImpl;

    public LoginResponse checkLoginCredentials(LoginRequest loginRequest) {

        log.info("checking credentials for email:{} and type :{}", loginRequest.getEmail(), loginRequest.getType());
        PatientMaster patientMaster = null;
        DoctorMaster doctorMaster = null;
        AdminMaster adminMaster = null;
        String password = null;
        String email = null;

        String type = loginRequest.getType();
        if ("patient".equalsIgnoreCase(type)) {
            patientMaster = patientControllerImpl.getByEmail(loginRequest.getEmail());
            if (ObjectUtils.isEmpty(patientMaster)) {
                return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "USER_NOT_FOUND");
            }
            password = patientMaster.getPatientPassword();
            email = patientMaster.getPatientEmail();
        } else if ("doctor".equalsIgnoreCase(type)) {
            doctorMaster = doctorControllerImpl.getByEmail(loginRequest.getEmail());
            if (ObjectUtils.isEmpty(doctorMaster)) {
                return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "USER_NOT_FOUND");
            }
            password = doctorMaster.getDoctorPassword();
            email = doctorMaster.getDoctorEmail();
        } else if ("admin".equalsIgnoreCase(type)) {
            adminMaster = adminControllerImpl.getByEmail(loginRequest.getEmail());
            if (ObjectUtils.isEmpty(adminMaster)) {
                return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "USER_NOT_FOUND");
            }
            password = adminMaster.getPassword();
            email = adminMaster.getEmail();
        } else {
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "INVALID_USER_TYPE");
        }

        // plain-text password comparison (consider hashing in future)
        if (loginRequest.getEmail().equals(email) && loginRequest.getPassword().equals(password)) {
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "PASS", "LOGIN_SUCCESSFUL");
        } else {
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "PASSWORD_INCORRECT");
        }
    }
}
