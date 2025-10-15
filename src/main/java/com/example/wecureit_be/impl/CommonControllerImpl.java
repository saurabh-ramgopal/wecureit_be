package com.example.wecureit_be.impl;

import com.example.wecureit_be.request.LoginRequest;
import com.example.wecureit_be.response.LoginResponse;
import com.example.wecureit_be.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.wecureit_be.repository.PatientMasterRepository;
import com.example.wecureit_be.repository.DoctorMasterRepository;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.entity.DoctorMaster;

@Slf4j
@Service
public class CommonControllerImpl {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PatientMasterRepository patientRepo;

    @Autowired
    DoctorMasterRepository doctorRepo;

    public LoginResponse checkLoginCredentials(LoginRequest loginRequest) {
        log.info("checking credentials for email:{} and type :{}", loginRequest.getEmail(), loginRequest.getType());
        try {
            String type = loginRequest.getType() == null ? "patient" : loginRequest.getType().toLowerCase();
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            if ("doctor".equals(type)) {
                DoctorMaster d = doctorRepo.getDoctorByEmail(email);
                if (d == null) return new LoginResponse(email, type, "FAIL", "USER_NOT_FOUND", null);
                if (!passwordEncoder.matches(password, d.getDoctorPassword())) {
                    return new LoginResponse(email, type, "FAIL", "PASSWORD_INCORRECT", null);
                }
                String token = jwtUtil.generateToken(email);
                return new LoginResponse(email, type, "PASS", "LOGIN_SUCCESSFUL", token);
            } else {
                PatientMaster p = patientRepo.getPatientByEmail(email);
                if (p == null) return new LoginResponse(email, type, "FAIL", "USER_NOT_FOUND", null);
                if (!passwordEncoder.matches(password, p.getPatientPassword())) {
                    return new LoginResponse(email, type, "FAIL", "PASSWORD_INCORRECT", null);
                }
                String token = jwtUtil.generateToken(email);
                return new LoginResponse(email, type, "PASS", "LOGIN_SUCCESSFUL", token);
            }
        } catch (Exception ex) {
            log.error("Unhandled exception during login for {}: {}", loginRequest.getEmail(), ex.toString());
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "INTERNAL_ERROR: " + ex.getMessage(), null);
        }
    }
}
