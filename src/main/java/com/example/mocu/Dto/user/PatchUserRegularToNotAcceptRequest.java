package com.example.mocu.Dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserRegularToNotAcceptRequest {
    // userId, 단골로 설정 가능한 목록에서 삭제할 가게의 storeId
    private long userId;
    private long storeId;
}
