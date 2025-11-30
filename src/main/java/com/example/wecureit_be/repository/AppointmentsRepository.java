package com.example.wecureit_be.repository;

import com.example.wecureit_be.entity.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, Integer> {

    @Query(value = "select a.* from appointments a " +
            "left join doctor_facility_availability dfa " +
            "on dfa.df_availability_id = a.df_availability_id " +
            "where dfa.doctor_master_id = :doctorMasterId " +
            "order by a.start_time asc ", nativeQuery = true)
    List<Appointments> getAppointmentByDocId (@Param("doctorMasterId") Integer doctorMasterId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE appointments " +
            "SET appointment_notes = :appointmentNote " +
            "where appointment_id = :appointmentId", nativeQuery = true)
    void updateAppointmentNote(@Param("appointmentId") Integer appointmentId,
                               @Param("appointmentNote") String appointmentNote);

    @Query(value = "SELECT a.* FROM appointments a " +
            "LEFT JOIN doctor_facility_availability dfa " +
            "ON dfa.df_availability_id = a.df_availability_id " +
            "WHERE dfa.doctor_master_id = :doctorMasterId " +
            "AND (a.date + a.start_time) >= NOW() " +
            "ORDER BY a.start_time ASC ", nativeQuery = true)
    List<Appointments> getAppointmentForNext2Weeks (@Param("doctorMasterId") Integer doctorMasterId);

    @Query(value = "SELECT a.* FROM appointments a " +
            "LEFT JOIN doctor_facility_availability dfa " +
            "ON dfa.df_availability_id = a.df_availability_id " +
            "WHERE dfa.doctor_master_id = :doctorMasterId " +
            "AND (a.date + a.end_time) <= NOW() " +
            "ORDER BY a.start_time ASC ", nativeQuery = true)
    List<Appointments> getPastAppointments (@Param("doctorMasterId") Integer doctorMasterId);


}
