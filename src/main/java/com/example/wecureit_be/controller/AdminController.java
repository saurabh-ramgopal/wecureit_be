package com.example.wecureit_be.controller;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.impl.AdminControllerImpl;
import com.example.wecureit_be.impl.DoctorControllerImpl;
import com.example.wecureit_be.request.AddDoctorRequest;
import com.example.wecureit_be.request.AdminLoginRequest;
import com.example.wecureit_be.request.DeleteDoctorRequest;
import com.example.wecureit_be.request.DoctorSpecialityRequest;
import com.example.wecureit_be.response.DoctorDetails;
import com.example.wecureit_be.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    DoctorControllerImpl doctorControllerImpl;

    @Autowired
    AdminControllerImpl adminControllerImpl;

    @PostMapping(value="/login")
    public LoginResponse checkLoginCredentials (@RequestBody AdminLoginRequest adminLoginRequest){
        return adminControllerImpl.checkLoginCredentials(adminLoginRequest);
    }

    @GetMapping(value="/getAllDoctors")
    public List<DoctorDetails> getAllDoctors() {
        return doctorControllerImpl.getAllDoctors();
    }

    @PostMapping(value="/addDoctor")
    public DoctorDetails addDoctor (@RequestBody AddDoctorRequest addDoctorRequest){
        return doctorControllerImpl.addDoctor(addDoctorRequest);
    }

    @PostMapping(value="/deleteDoctor")
    public DoctorMaster deleteDoctor (@RequestBody DeleteDoctorRequest deleteDoctorRequest){
        return doctorControllerImpl.deleteDoctor(deleteDoctorRequest);
    }

    @PostMapping(value="/updateDoctorSpeciality")
    public DoctorDetails updateDoctorSpeciality (@RequestBody DoctorSpecialityRequest doctorSpecialityRequest){
        return doctorControllerImpl.updateDoctorSpeciality(doctorSpecialityRequest);
    }
}