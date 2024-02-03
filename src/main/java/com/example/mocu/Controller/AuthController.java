package com.example.mocu.Controller;

import com.example.mocu.Service.AuthService;
import com.example.mocu.auth.AuthTokens;
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

    @PostMapping("/kakao")
    public ResponseEntity<AuthTokens> loginKakao(@RequestBody KakaoLoginParams params) {
        log.info("[AuthController.loginKakao] params: {}", params);

        return ResponseEntity.ok(authService.login(params));
    }
}