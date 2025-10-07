package com.example.wecureit_be;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    DoctorMasterRepository doctorMasterRepository;

    @PostMapping(value="/add")
    public DoctorMaster addDoc (@RequestBody DoctorMaster doctorMaster){
        return doctorMasterRepository.save(doctorMaster);
    }

    @GetMapping(value="/get")
    public List<DoctorMaster> getAllDoctors() {
        return doctorMasterRepository.findAll();
    }

}
