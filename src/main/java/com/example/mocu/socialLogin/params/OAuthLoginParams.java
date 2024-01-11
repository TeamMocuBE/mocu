package com.example.mocu.socialLogin.params;

import com.example.mocu.socialLogin.util.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();

    MultiValueMap<String, String> makeBody();
}
