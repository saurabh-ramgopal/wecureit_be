package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class AddAppointmentNoteRequest {
    Integer appointmentId;
    String appointmentNote;
}
