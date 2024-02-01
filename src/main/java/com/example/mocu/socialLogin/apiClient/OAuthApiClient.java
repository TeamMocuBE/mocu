package com.example.mocu.socialLogin.apiClient;

import com.example.mocu.socialLogin.infoResponse.OAuthInfoResponse;
import com.example.mocu.socialLogin.params.OAuthLoginParams;
import com.example.mocu.socialLogin.util.OAuthProvider;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();

    String requestAccessToken(OAuthLoginParams params);

    OAuthInfoResponse requestOauthInfo(String accessToken);
}
