package com.example.wecureit_be.response;

import lombok.Value;

import java.time.LocalTime;

@Value
public class TimeSlot {
    LocalTime start;
    LocalTime end;
}
