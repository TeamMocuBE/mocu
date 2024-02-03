package com.example.mocu.Dto.user;

import com.example.mocu.auth.AuthTokens;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private long userId;

    private AuthTokens authTokens;
}
