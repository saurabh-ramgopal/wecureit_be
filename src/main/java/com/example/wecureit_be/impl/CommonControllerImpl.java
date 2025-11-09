package com.example.wecureit_be.impl;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.request.CommonLoginRequest;
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

    public LoginResponse checkLoginCredentials(CommonLoginRequest commonLoginRequest) {
        try {
            final String emailIn = commonLoginRequest == null ? null : commonLoginRequest.getEmail();
            final String typeIn = commonLoginRequest == null ? null : commonLoginRequest.getType();
            final String passwordIn = commonLoginRequest == null ? null : commonLoginRequest.getPassword();
            log.info("checking credentials for email:{} and type :{}", emailIn, typeIn);

            // Handle admin separately by delegating to AdminControllerImpl
            if ("admin".equalsIgnoreCase(typeIn)) {
                // AdminControllerImpl expects AdminLoginRequest; we can reuse its logic by calling its method via a small adapter.
                // Build a simple AdminLoginRequest inline to delegate
                com.example.wecureit_be.request.AdminLoginRequest adminReq = new com.example.wecureit_be.request.AdminLoginRequest();
                adminReq.setEmail(emailIn);
                adminReq.setPassword(passwordIn);
                return adminControllerImpl.checkLoginCredentials(adminReq);
            }

            // Patient login
            if ("patient".equalsIgnoreCase(typeIn)) {
                PatientMaster patientMaster = patientControllerImpl.getByEmail(emailIn);
                if (ObjectUtils.isEmpty(patientMaster)) {
                    return new LoginResponse(emailIn, typeIn, "FAIL", "The user does not exist in system. Please sign up.");
                }
                String password = patientMaster.getPatientPassword();
                String email = patientMaster.getPatientEmail();
                if (emailIn != null && emailIn.equals(email) && passwordIn != null && passwordIn.equals(password)) {
                    return new LoginResponse(emailIn, typeIn, "PASS", "LOGIN_SUCCESSFUL");
                }
                return new LoginResponse(emailIn, typeIn, "FAIL", "Password incorrect, please check credentials.");
            }

            // Doctor or other types: try doctor lookup
            DoctorMaster doctorMaster = doctorControllerImpl.getByEmail(emailIn);
            if (ObjectUtils.isEmpty(doctorMaster)) {
                return new LoginResponse(emailIn, typeIn, "FAIL", "The user does not exist in system. Please sign up.");
            }
            String password = doctorMaster.getDoctorPassword();
            String email = doctorMaster.getDoctorEmail();
            if (emailIn != null && emailIn.equals(email) && passwordIn != null && passwordIn.equals(password)) {
                return new LoginResponse(emailIn, typeIn, "PASS", "LOGIN_SUCCESSFUL");
            }
            return new LoginResponse(emailIn, typeIn, "FAIL", "Password incorrect, please check credentials.");
        } catch (Exception ex) {
            log.error("Exception while checking credentials", ex);
            String reason = ex.getMessage() == null ? "Internal error during login" : ex.getMessage();
            return new LoginResponse(commonLoginRequest == null ? null : commonLoginRequest.getEmail(), commonLoginRequest == null ? null : commonLoginRequest.getType(), "FAIL", reason);
        }
    }
}
