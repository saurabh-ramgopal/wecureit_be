package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.DoctorFacilityAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorFacilityAvailabilityRepository extends JpaRepository<DoctorFacilityAvailability, String> {

    @Query(value = "SELECT * FROM doctor_facility_availability WHERE doctor_master_id = :doctorId and is_active = true ", nativeQuery = true)
    List<DoctorFacilityAvailability> getAvailableFacilityById(@Param("doctorId") Integer doctorId);

    @Query(value = "SELECT * FROM doctor_facility_availability WHERE facility_master_id = :facilityMasterId and is_active = true", nativeQuery = true)
    List<DoctorFacilityAvailability> getAvailableDoctorsById(@Param("facilityMasterId") String facilityMasterId);

}