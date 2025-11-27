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

    @Query(value = "SELECT * FROM doctor_facility_availability WHERE facility_master_id = :facilityMasterId" +
            " and doctor_master_id = :doctorMasterId and is_active = true", nativeQuery = true)
    List<DoctorFacilityAvailability> getAvailabilityByDocIdAndFacId(@Param("doctorMasterId") Integer doctorMasterId,
                                                                    @Param("facilityMasterId") String facilityMasterId);

    @Query(value = "select dfa.* from doctor_facility_availability dfa " +
            "join practising_speciality ps on dfa.df_availability_id = ps.df_availability_id " +
            "where dfa.facility_master_id = :facilityMasterId and ps.speciality_master_id = :specialityMasterId " +
            "and dfa.is_active = true ", nativeQuery = true)
    List<DoctorFacilityAvailability> getAvailabilityByFacIdAndSpecId(@Param("facilityMasterId") String facilityMasterId,
                                                                     @Param("specialityMasterId") String specialityMasterId);

    @Query(value = "select dfa.* from doctor_facility_availability dfa " +
            "join practising_speciality ps on dfa.df_availability_id = ps.df_availability_id " +
            "where dfa.doctor_master_id = :doctorMasterId and ps.speciality_master_id = :specialityMasterId " +
            "and dfa.is_active = true ", nativeQuery = true)
    List<DoctorFacilityAvailability> getAvailabilityByDocIdAndSpecId(@Param("doctorMasterId") Integer doctorMasterId,
                                                                     @Param("specialityMasterId") String specialityMasterId);

}