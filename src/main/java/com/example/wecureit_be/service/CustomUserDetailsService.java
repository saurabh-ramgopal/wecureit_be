package com.example.wecureit_be.service;

import com.example.wecureit_be.entity.DoctorMaster;
import com.example.wecureit_be.entity.PatientMaster;
import com.example.wecureit_be.repository.DoctorMasterRepository;
import com.example.wecureit_be.repository.PatientMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PatientMasterRepository patientRepo;

    @Autowired
    private DoctorMasterRepository doctorRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PatientMaster p = patientRepo.getPatientByEmail(username);
        if (p != null && !ObjectUtils.isEmpty(p.getPatientMasterId())) {
            return User.withUsername(p.getPatientEmail())
                    .password(p.getPatientPassword())
                    .authorities(new String[0])
                    .build();
        }

        DoctorMaster d = doctorRepo.getDoctorByEmail(username);
        if (d != null && !ObjectUtils.isEmpty(d.getDoctorMasterId())) {
            return User.withUsername(d.getDoctorEmail())
                    .password(d.getDoctorPassword())
                    .authorities(new String[0])
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
