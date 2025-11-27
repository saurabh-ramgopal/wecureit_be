package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.DoctorSpecialityMapping;
import com.example.wecureit_be.entity.DoctorSpecialityMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DoctorSpecialityMappingRepository extends JpaRepository<DoctorSpecialityMapping, DoctorSpecialityMappingId> {

    @Query(value = "select * from doctor_speciality_mapping " +
            "where doctor_master_id = :doctorMasterId ", nativeQuery = true)
    List<DoctorSpecialityMapping> getDoctorSpecialityByDoctorId(@Param("doctorMasterId") Integer doctorMasterId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM doctor_speciality_mapping WHERE doctor_master_id = :doctorMasterId ", nativeQuery = true)
    int deleteDoctorAllSpeciality(@Param("doctorMasterId") Integer doctorMasterId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO public.doctor_speciality_mapping (doctor_master_id, speciality_master_id, state_code) " +
            "VALUES( :doctorMasterId , :specialityMasterId , :stateCode );", nativeQuery = true)
    void insertIntoDoctorSpecialityMapping(@Param("doctorMasterId") Integer doctorMasterId,
                                             @Param("specialityMasterId") String specialityMasterId,
                                             @Param("stateCode") String stateCode);

    @Query(value = "select * from doctor_speciality_mapping " +
            "where speciality_master_id = :specialityMasterId ", nativeQuery = true)
    List<DoctorSpecialityMapping> getDoctorsBySpecialityId(@Param("specialityMasterId") String specialityMasterId);

    @Query(value = "select * from doctor_speciality_mapping " +
            "where doctor_master_id = :doctorMasterId and state_code = :stateCode ", nativeQuery = true)
    List<DoctorSpecialityMapping> getSpecialityByDoctorIdAndStateCode(@Param("doctorMasterId") Integer doctorMasterId,
                                                                      @Param("stateCode") String stateCode);


}
