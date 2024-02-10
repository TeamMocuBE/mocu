package com.example.mocu.Dto.owner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerStampResponse {
    private String userImage;
    private String userName;
    private int numOfStamp;
    private int maxStamp;
    private int useCount;
}
