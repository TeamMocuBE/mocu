package com.example.mocu.Controller;

import com.example.mocu.Dto.Push.PushRequest;
import com.example.mocu.Service.PushService;
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
@RequestMapping("/api/push")
public class PushController {
    private final PushService pushService;

    @PostMapping("/kakao")
    public ResponseEntity<String> sendPushMessage(@RequestBody PushRequest pushRequest){
        log.info("[PushController.sendPushMessage]");

        try{
            pushService.sendPushMessage(pushRequest);

            return ResponseEntity.ok("푸시 알림 전송 완료.");
        } catch (RuntimeException e){
            return ResponseEntity.ok("푸시 알림 전송 실패.");
        }

    }
}
