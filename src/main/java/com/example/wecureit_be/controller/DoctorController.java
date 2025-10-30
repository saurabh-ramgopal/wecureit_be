package com.example.wecureit_be.controller;

import com.example.wecureit_be.impl.DoctorControllerImpl;
import com.example.wecureit_be.response.DoctorDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
