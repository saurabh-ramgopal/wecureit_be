package com.example.wecureit_be.response;

import lombok.Data;
import java.util.List;

@Data
public class RoomDetail {
    private Integer roomNumber;
    private String roomLabel;
    private List<String> specialityList; // list of speciality ids (codes)
}
