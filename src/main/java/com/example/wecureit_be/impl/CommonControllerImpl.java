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

@Slf4j
@Service
public class CommonControllerImpl {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    public LoginResponse checkLoginCredentials(LoginRequest loginRequest) {
        log.info("checking credentials for email:{} and type :{}", loginRequest.getEmail(), loginRequest.getType());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            String token = jwtUtil.generateToken(loginRequest.getEmail());
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "PASS", "LOGIN_SUCCESSFUL", token);
        } catch (BadCredentialsException ex) {
            return new LoginResponse(loginRequest.getEmail(), loginRequest.getType(), "FAIL", "PASSWORD_INCORRECT", null);
        }
    }
}
