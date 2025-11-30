package com.example.wecureit_be.response;

import lombok.Value;

import java.time.LocalDate;

@Value
public class FetchDatesResponse {
    String dfAvailabilityId;
    LocalDate availableDate;
    Boolean isFilled;
}
