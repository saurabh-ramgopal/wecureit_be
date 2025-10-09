package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.PatientMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMasterRepository extends JpaRepository<PatientMaster, String> {
}
