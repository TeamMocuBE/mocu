package com.example.mocu.Service;

import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dto.Push.PushRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@Service
@RequiredArgsConstructor
public class PushService {
    private final OwnerDao ownerDao;
    private final RestTemplate restTemplate;

    @Value("${oauth.kakao.url.api}")
    private String apiUrl;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    public void sendPushMessage(PushRequest pushRequest) {
        log.info("[PushService.sendPushMessage]");

        String url = apiUrl + "/v2/push/send";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "KakaoAK " + clientId);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("uuids", pushRequest.getOwnerId().);


    }
}
