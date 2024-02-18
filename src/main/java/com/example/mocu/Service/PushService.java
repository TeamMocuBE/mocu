package com.example.mocu.Service;

import com.example.mocu.Dto.Push.PushRequestToOwner;
import com.example.mocu.Dto.Push.PushRequestToUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
@Component
public class PushService {
    private final RestTemplate restTemplate;

    @Value("${oauth.kakao.url.api}")
    private String apiUrl;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    public void sendPushMessageToOwner(PushRequestToOwner pushRequestToOwner) {
        log.info("[PushService.sendPushMessage]");

        log.info("url");
        String url = apiUrl + "/v2/push/send";

        log.info("http header 구성");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "KakaoAK " + clientId);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        log.info("owner의 카카오 uuid 붙이기");
        // -> uuid 값으로 바꿔야 함
        body.add("uuids", pushRequestToOwner.getOwnerUuid());

        log.info("body작성 시작");
        body.add("push_message", makeMessageBodyToOwner(pushRequestToOwner));

        log.info("requestEntity 구성");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, httpHeaders);

        log.info("responseEntity 받아오기");
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()){
            log.info("푸시 알림 전송 완료.");
        }
        else{
            log.error("푸시 알림 전송 실패. 응답 코드 : {}", responseEntity.getStatusCodeValue());
        }
    }

    private String makeMessageBodyToOwner(PushRequestToOwner pushRequestToOwner) {
        log.info("[PushService.makeMessageBody]");

        Map<String, Object> pushMessage = new HashMap<>();

        log.info("custom_field 구성");
        // custom_field 구성
        Map<String, Object> customField = new HashMap<>();
        customField.put("requestId", pushRequestToOwner.getRequestId());
        customField.put("userId", pushRequestToOwner.getUserId());
        customField.put("storeId", pushRequestToOwner.getStoreId());

        log.info("notification 구성");
        // notification 구성
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "고객 요청");
        notification.put("body", pushRequestToOwner.getUserName() +
                "님의 적립 요청이 있습니다. 수락하시겠습니까?\n일시 : " +
                pushRequestToOwner.getCreatedDate() + "\n장소 : " +
                pushRequestToOwner.getStoreAddress());

        log.info("push_message 객체에 custom, noti 추가");
        // push_message 객체에 custom_field, notification 추가
        pushMessage.put("custom_field", customField);
        pushMessage.put("notification", notification);

        log.info("JSON 문자열로 변환");
        // ObjectMapper를 사용해 JSON 문자열로 변화
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(pushMessage);
        } catch (JsonProcessingException e){
            log.error("Error converting PushMessage to JSON string: {}", e.getMessage());
            return null;
        }
    }


    public void registerPushToken(String uuid, String deviceId, String deviceToken) {
        log.info("[PushService.registerPushToken]");

        String url = apiUrl + "/v2/push/register";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "KakaoAK " + clientId);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        // uuid 값 얻어 와야 함
        body.add("uuid", uuid);
        body.add("device_id", deviceId);
        body.add("push_type", "fcm");
        body.add("push_token", deviceToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, httpHeaders);

        try{
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()){
                log.info("푸시 토큰 등록 성공");
            }
            else{
                log.error("푸시 토큰 등록 실패. 응답 코드 : {}", responseEntity.getStatusCodeValue());
            }
        } catch (HttpClientErrorException e){
            log.error("푸시 토큰 등록 실패. 에러 메시지 : {}", e.getMessage());
        }
    }

    public void sendPushMessageToUser(PushRequestToUser pushRequestToUser) {
        log.info("[PushService.sendPushMessageToUser]");

        log.info("url");
        String url = apiUrl + "/v2/push/send";

        log.info("http header 구성");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "KakaoAK " + clientId);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        log.info("user의 카카오 uuid 붙이기");
        body.add("uuids", pushRequestToUser.getUserUuid());

        log.info("push_message 작성");
        body.add("push_message", makeMessageBodyToUser(pushRequestToUser));

        log.info("requestEntity 구성");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, httpHeaders);

        log.info("responseEntity 받아오기");
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()){
            log.info("푸시 알림 전송 완료.");
        }
        else{
            log.error("푸시 알림 전송 실패. 응답 코드 : {}", responseEntity.getStatusCodeValue());
        }
    }

    public String makeMessageBodyToUser(PushRequestToUser pushRequestToUser){
        log.info("[PushService.makeMessageBodyToUser]");

        Map<String, Object> pushMessage = new HashMap<>();

        log.info("custom_field 구성");
        Map<String, Object> customField = new HashMap<>();
        customField.put("stampId", pushRequestToUser.getStampId());
        customField.put("numOfStamp", pushRequestToUser.getNumOfStamp());
        customField.put("maxStamp", pushRequestToUser.getMaxStamp());
        customField.put("storeName", pushRequestToUser.getStoreName());
        customField.put("dueDate", pushRequestToUser.isDueDate());
        customField.put("numOfCouponAvailable", pushRequestToUser.getNumOfCouponAvailable());

        log.info("notification 구성");
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "스탬프 적립 완료!");
        notification.put("body", "가게에서 적립 요청을 수락했습니다.");

        log.info("push_message 객체에 custom, noti 추가");
        pushMessage.put("custom_field", customField);
        pushMessage.put("notification", notification);

        log.info("JSON 문자열로 변환");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(pushMessage);
        } catch (JsonProcessingException e){
            log.error("Error converting PushMessage to JSON string: {}", e.getMessage());
            return null;
        }
    }
}
