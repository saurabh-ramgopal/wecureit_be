package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.AdminMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminMasterRepository extends JpaRepository<AdminMaster, String> {
}
