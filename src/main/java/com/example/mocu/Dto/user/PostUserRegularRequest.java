package com.example.mocu.Dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserRegularRequest {
    // userId, storeId, 단골로 등록할지 말지 여부
    private long userId;
    private long storeId;
    private boolean request;
}
