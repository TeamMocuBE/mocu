package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.Push.PushRequestToOwner;
import com.example.mocu.Dto.Push.PushRequestToUser;
import com.example.mocu.Service.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/stamp-to-owner")
    public BaseResponse<String> sendMessageToOwner(@RequestBody PushRequestToOwner pushRequestToOwner){
        log.info("[PushController.sendMessageToOwner]");

        pushService.sendPushMessageToOwner(pushRequestToOwner);

        try{
            pushService.sendPushMessageToOwner(pushRequestToOwner);

            return new BaseResponse<>("푸시 알림 전송 완료.");
        } catch (RuntimeException e){
            return new BaseResponse<>("푸시 알림 전송 중 에러 발생.");
        }
    }

    @PostMapping("/stamp-to-user")
    public BaseResponse<String> sendMessageToUser(@RequestBody PushRequestToUser pushRequestToUser){
        log.info("[PushController.sendMessageToUser]");

        pushService.sendPushMessageToUser(pushRequestToUser);

        try {
            pushService.sendPushMessageToUser(pushRequestToUser);

            return new BaseResponse<>("푸시 알림 전송 완료.");
        } catch (RuntimeException e){
            return new BaseResponse<>("푸시 알림 전송 실패.");
        }

    }
}
