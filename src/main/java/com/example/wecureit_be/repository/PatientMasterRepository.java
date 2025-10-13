package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.PatientMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMasterRepository extends JpaRepository<PatientMaster, String> {

    @Query(value = "SELECT * FROM patient_master WHERE patient_master_id = :patientId", nativeQuery = true)
    PatientMaster getPatientById(@Param("patientId") Integer patientId);
}
