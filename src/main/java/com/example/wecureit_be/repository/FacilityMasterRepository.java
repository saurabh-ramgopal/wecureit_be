package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.FacilityMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMasterRepository extends JpaRepository<FacilityMaster, String> {

    @Query(value = "SELECT * FROM facility_master", nativeQuery = true)
    List<FacilityMaster> getAllFacility();

    @Query(value = "SELECT * FROM facility_master where facility_master_id = :facilityMasterId", nativeQuery = true)
    FacilityMaster getFacilityById(@Param("facilityMasterId") String facilityMasterId);
}
