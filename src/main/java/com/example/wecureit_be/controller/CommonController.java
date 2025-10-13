package com.example.wecureit_be.controller;

import com.example.wecureit_be.request.LoginRequest;
import com.example.wecureit_be.impl.CommonControllerImpl;
import com.example.wecureit_be.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    CommonControllerImpl commonControllerImpl;

    @PostMapping(value="/login")
    public LoginResponse checkLoginCredentials (@RequestBody LoginRequest loginRequest){
        return commonControllerImpl.checkLoginCredentials(loginRequest);
    }


}
