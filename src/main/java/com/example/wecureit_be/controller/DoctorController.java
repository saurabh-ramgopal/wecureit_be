package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.impl.DoctorControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @PostMapping(value="/addOrUpdate")
    public DoctorMaster addOrUpdate (@RequestBody DoctorMaster doctorMaster){
        return doctorControllerImpl.addOrUpdate(doctorMaster);
    }

    @GetMapping(value="/getById")
    public DoctorMaster getById(@RequestParam String doctorId) {
        return doctorControllerImpl.getById(doctorId);
    }

}
