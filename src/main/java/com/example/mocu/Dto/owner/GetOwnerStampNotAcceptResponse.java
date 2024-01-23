package com.example.mocu.Dto.owner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetOwnerStampNotAcceptResponse {
    // 스탬프 적립 요청을 미수락한 user 이름을 return
    private String userName;
    private String createdDate;
    private String status;
}
