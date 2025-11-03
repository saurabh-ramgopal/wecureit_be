package com.example.wecureit_be.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CardRequest {
    private String pan;
    private String brand;
    private int expMonth;
    private int expYear;
}
