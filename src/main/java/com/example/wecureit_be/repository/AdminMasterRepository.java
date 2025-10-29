package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.AdminMaster;
import com.example.wecureit_be.entity.PatientMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminMasterRepository extends JpaRepository<AdminMaster, Integer> {

    @Query(value = "SELECT * FROM admin_master WHERE admin_master_id = :adminId", nativeQuery = true)
    AdminMaster getAdminById(@Param("adminId") Integer adminId);

    @Query(value = "SELECT * FROM admin_master WHERE admin_email = :adminEmail", nativeQuery = true)
    AdminMaster getAdminByEmail(@Param("adminEmail") String adminEmail);
}
