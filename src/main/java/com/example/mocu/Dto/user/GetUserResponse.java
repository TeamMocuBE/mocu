package com.example.mocu.Dto.user;

import com.example.mocu.socialLogin.util.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {
    private String nickname;
    private String email;
    private String userImage;
    private String status;
    private OAuthProvider oAuthProvider;
}
