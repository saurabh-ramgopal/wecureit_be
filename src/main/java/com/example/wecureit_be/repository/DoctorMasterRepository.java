package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.DoctorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, String> {
}
