package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.Push.PostRegisterPushTokenRequest;
import com.example.mocu.Dto.user.AuthResponse;
import com.example.mocu.Service.AuthService;
import com.example.mocu.socialLogin.params.KakaoLoginParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/kakao/user")
    public ResponseEntity<AuthResponse> userLoginKakao(@RequestBody KakaoLoginParams params) {
        log.info("[AuthController.loginKakao] params: {}", params);
        AuthResponse authResponse = authService.userLogin(params);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/kakao/owner")
    public ResponseEntity<AuthResponse> ownerLoginKakao(@RequestBody KakaoLoginParams params) {
        log.info("[AuthController.loginKakao] params: {}", params);
        AuthResponse authResponse = authService.ownerLogin(params);

        return ResponseEntity.ok(authResponse);
    }

    // 프론트한테서 디바이스ID, 디바이스토큰, userId 값 받아오기
    @PostMapping("/register/push-token")
    public BaseResponse<String> registerPushToken(@RequestBody PostRegisterPushTokenRequest postRegisterPushTokenRequest){
        log.info("[AuthController.registerPushToken]");

        try {
            authService.registerPushToken(postRegisterPushTokenRequest);
            return new BaseResponse<>("푸시 토큰 등록 성공.");
        } catch (RuntimeException e){
            return new BaseResponse<>("푸시 토큰 등록 중 에러 발생.");
        }

    }
}