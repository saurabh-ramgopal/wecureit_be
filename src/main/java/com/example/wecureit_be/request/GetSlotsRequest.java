package com.example.wecureit_be.request;

import lombok.Data;

@Data
public class GetSlotsRequest {
    Integer duration;
    String dfAvailabilityId;
}