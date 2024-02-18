package com.example.mocu.socialLogin.params;

import com.example.mocu.socialLogin.util.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginParams implements OAuthLoginParams {
    private String authorizationCode;

    // push token 등록
    private String deviceId;
    private String deviceToken;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        log.info("[KakaoLoginParams.makeBody]");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}
