package com.example.wecureit_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.impl.AdminControllerImpl;
import com.example.wecureit_be.impl.DoctorControllerImpl;
import com.example.wecureit_be.request.AddDoctorRequest;
import com.example.wecureit_be.request.AdminLoginRequest;
import com.example.wecureit_be.request.DeleteDoctorRequest;
import com.example.wecureit_be.request.DoctorSpecialityRequest;
import com.example.wecureit_be.response.DoctorDetails;
import com.example.wecureit_be.response.LoginResponse;

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
    public org.springframework.http.ResponseEntity<?> addDoctor (@RequestBody AddDoctorRequest addDoctorRequest){
        try {
            DoctorDetails dd = doctorControllerImpl.addDoctor(addDoctorRequest);
            return org.springframework.http.ResponseEntity.ok(dd);
        } catch (IllegalArgumentException iae) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("message", iae.getMessage());
            return org.springframework.http.ResponseEntity.badRequest().body(error);
        } catch (Exception ex) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("message", "Internal error while adding doctor");
            return org.springframework.http.ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping(value="/deleteDoctor")
    public ResponseEntity<?> deleteDoctor (@RequestBody DeleteDoctorRequest deleteDoctorRequest){
        DoctorMaster result = doctorControllerImpl.deleteDoctor(deleteDoctorRequest);
        if (result == null) {
            // return empty JSON object so frontend can safely parse response.json()
            return ResponseEntity.ok().body(new java.util.HashMap<>());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(value="/updateDoctorSpeciality")
    public DoctorDetails updateDoctorSpeciality (@RequestBody DoctorSpecialityRequest doctorSpecialityRequest){
        return doctorControllerImpl.updateDoctorStateSpecialities(doctorSpecialityRequest.getDoctorMasterId(),
                doctorSpecialityRequest.getDoctorStateSpeciality());
    }
}