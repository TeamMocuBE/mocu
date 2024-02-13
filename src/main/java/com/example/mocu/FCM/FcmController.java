package com.example.mocu.FCM;

import com.example.mocu.Common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FcmController {
    private final FcmService fcmService;

    @PostMapping("/api/fcm")
    public ResponseEntity pushMessage(@RequestBody FcmRequest fcmRequest) throws IOException {
        fcmService.sendMessageTo(
                fcmRequest.getTargetToken(),
                fcmRequest.getTitle(),
                fcmRequest.getBody());

        return ResponseEntity.ok().build();
    }
}
