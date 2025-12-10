package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.FacilityMaster;
import com.example.wecureit_be.response.DoctorFacilities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMasterRepository extends JpaRepository<FacilityMaster, String> {

    @Query(value = "SELECT * FROM facility_master where is_active = true ", nativeQuery = true)
    List<FacilityMaster> getAllFacility();

    @Query(value = "SELECT * FROM facility_master where facility_master_id = :facilityMasterId", nativeQuery = true)
    FacilityMaster getFacilityById(@Param("facilityMasterId") String facilityMasterId);

    @Query(
        value = """
            WITH doctor_state_specs AS (
                SELECT 
                    dsm.state_code,
                    dsm.speciality_master_id
                FROM doctor_speciality_mapping dsm
                WHERE dsm.doctor_master_id = :doctorId
            )
            SELECT DISTINCT fm.facility_master_id , fsm.speciality_master_id
            FROM facility_master fm
            JOIN facility_speciality_mapping fsm 
                ON fm.facility_master_id = fsm.facility_master_id
            JOIN doctor_state_specs dss
                ON dss.state_code = fm.state_code
                AND dss.speciality_master_id = fsm.speciality_master_id
                AND fm.is_active = true
            """,
        nativeQuery = true
    )
    List<DoctorFacilities> getFacilitiesForDoctor(@Param("doctorId") Integer doctorId);
}
