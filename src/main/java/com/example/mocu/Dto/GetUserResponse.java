package com.example.mocu.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {
    private String name;
    private String email;
    private String userImage;
    private String status;
}
