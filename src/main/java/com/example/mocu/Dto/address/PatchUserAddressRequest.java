package com.example.mocu.Dto.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserAddressRequest {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
}
