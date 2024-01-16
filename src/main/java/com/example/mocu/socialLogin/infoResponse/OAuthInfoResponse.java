package com.example.mocu.socialLogin.infoResponse;

import com.example.mocu.socialLogin.util.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();

    String getName();

    String getProfileImageUrl();

    OAuthProvider getOAuthProvider();
}
