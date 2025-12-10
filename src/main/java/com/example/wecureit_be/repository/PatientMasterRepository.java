package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.Appointments;
import com.example.wecureit_be.entity.PatientMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PatientMasterRepository extends JpaRepository<PatientMaster, Integer> {

    @Query(value = "SELECT * FROM patient_master WHERE patient_master_id = :patientId", nativeQuery = true)
    PatientMaster getPatientById(@Param("patientId") Integer patientId);

    @Query(value = "SELECT * FROM patient_master WHERE patient_email = :patientEmail", nativeQuery = true)
    PatientMaster getPatientByEmail(@Param("patientEmail") String patientEmail);

    @Query(value = """
    SELECT *
    FROM appointments
    WHERE patient_master_id = :patientId
    AND is_active = true
    AND (
            date > CURRENT_DATE
            OR (date = CURRENT_DATE AND end_time > CURRENT_TIME)
        )
    ORDER BY date ASC, start_time ASC
    """, nativeQuery = true)
    List<Appointments> getUpcomingAppointmentsByPatientId(@Param("patientId") Integer patientId);


    @Query(value = """
    SELECT *
    FROM appointments
    WHERE patient_master_id = :patientId
    AND is_active = true
    AND (
            date < CURRENT_DATE
            OR (date = CURRENT_DATE AND end_time < CURRENT_TIME)
        )
    ORDER BY date DESC, start_time DESC
    """, nativeQuery = true)
    List<Appointments> getOldAppointmentsByPatientId(@Param("patientId") Integer patientId);

    @Query(value = "SELECT * FROM appointments WHERE patient_master_id = :patientId and is_active = false ", nativeQuery = true)
    List<Appointments> getCancelledAppointmentsByPatientId(@Param("patientId") Integer patientId);



}
