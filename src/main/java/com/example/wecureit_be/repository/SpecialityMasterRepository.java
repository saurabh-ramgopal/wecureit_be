package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.SpecialityMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialityMasterRepository extends JpaRepository<SpecialityMaster, String> {

    @Query(value = "SELECT * FROM speciality_master;", nativeQuery = true)
    List<SpecialityMaster> getAllSpeciality();

    @Query(value = "SELECT * FROM speciality_master where speciality_master_id = :specialityMasterId ", nativeQuery = true)
    SpecialityMaster getSpecialityById(@Param("specialityMasterId") String specialityMasterId);

}
