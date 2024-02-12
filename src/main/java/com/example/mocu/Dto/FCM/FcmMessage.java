package com.example.mocu.Dto.FCM;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.logging.log4j.message.Message;

import javax.management.Notification;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessage {
    private boolean validateOnly;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message{
        private Notification notification;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification{
        private String title;
        private String body;
        private String image;
    }
}
