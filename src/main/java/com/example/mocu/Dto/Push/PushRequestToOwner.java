package com.example.mocu.Dto.Push;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PushRequestToOwner {
    private String ownerUuid;
    private long requestId;
    private long userId;
    private long storeId;
    private String createdDate;
    private String storeAddress;
    private String userName;
}
