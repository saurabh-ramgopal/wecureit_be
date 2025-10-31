package com.example.wecureit_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wecureit_be.impl.DoctorControllerImpl;
import com.example.wecureit_be.response.DoctorDetails;


@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @GetMapping(value="/getById")
    public DoctorDetails getById(@RequestParam Integer doctorId) {
        return doctorControllerImpl.getById(doctorId);
    }

}
