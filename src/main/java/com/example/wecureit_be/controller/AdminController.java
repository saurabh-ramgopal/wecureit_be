package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.impl.DoctorControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @GetMapping(value="/getAllDoctors")
    public List<DoctorMaster> getAllDoctors() {
        return doctorControllerImpl.getAllDoctors();
    }

}
