package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.DoctorFacilityAvailability;
import com.example.wecureit_be.entity.PractisingSpeciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PractisingSpecialityRepository extends JpaRepository<PractisingSpeciality, DoctorFacilityAvailability> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO public.practising_speciality (df_availability_id, speciality_master_id) " +
            "VALUES( :dfAvailabilityId , :specialityMasterId );", nativeQuery = true)
    void insertIntoPractisingSpeciality(@Param("dfAvailabilityId") String dfAvailabilityId,
                                        @Param("specialityMasterId") String specialityMasterId);


    @Query(value = "SELECT * FROM practising_speciality WHERE df_availability_id = :dfAvailabilityId", nativeQuery = true)
    List<PractisingSpeciality> getSpecialitiesByDfaId(@Param("dfAvailabilityId") String dfAvailabilityId);

    @Query(value = "SELECT * FROM practising_speciality WHERE speciality_master_id = :specialityMasterId", nativeQuery = true)
    List<PractisingSpeciality> getDfaIdBySpecialty(@Param("specialityMasterId") String specialityMasterId);

    @Query(value = "SELECT * FROM practising_speciality ", nativeQuery = true)
    List<PractisingSpeciality> getAllDfaId ();

}
