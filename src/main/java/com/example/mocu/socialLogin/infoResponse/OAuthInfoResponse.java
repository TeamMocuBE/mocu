package com.example.mocu.socialLogin.infoResponse;

import com.example.mocu.socialLogin.util.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();

    String getNickname();

    String getProfileImageUrl();

    OAuthProvider getOAuthProvider();
}
