package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.DoctorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, String> {

    @Query(value = "SELECT * FROM doctor_master WHERE doctor_master_id = :doctorId", nativeQuery = true)
    DoctorMaster getDoctorById(@Param("doctorId") String doctorId);

    @Query(value = "SELECT * FROM doctor_master WHERE doctor_email = :doctorEmail", nativeQuery = true)
    DoctorMaster getDoctorByEmail(@Param("doctorEmail") String doctorEmail);
}
