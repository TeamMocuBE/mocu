package com.example.mocu.Dto.Push;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PushRequest {
    private long ownerId;
    private long requestId;
    private long userId;
    private long storeId;
    private String createdDate;
    private String storeAddress;
    private String userName;
    private String reward;
}
