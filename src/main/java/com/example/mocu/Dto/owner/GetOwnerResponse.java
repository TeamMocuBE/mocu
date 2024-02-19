package com.example.mocu.Dto.owner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOwnerResponse {
    private Long ownerId;
    private String name;
    private String email;
    private String ownerImage;
    private String status;
    private String oAuthProvider;
}
