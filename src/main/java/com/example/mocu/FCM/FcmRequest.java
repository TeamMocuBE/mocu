package com.example.mocu.FCM;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FcmRequest {
    private String targetToken;
    private String title;
    private String body;
}
