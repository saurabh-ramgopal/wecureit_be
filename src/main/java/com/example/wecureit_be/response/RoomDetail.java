package com.example.wecureit_be.response;

import java.util.List;

import lombok.Data;

@Data
public class RoomDetail {
    private Integer roomNumber;
    private String roomLabel;
    private List<String> specialityList; // list of speciality ids (codes)
}
