package com.example.mocu.Dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMyStoreListResponse {
    private int availableCount;

    private List<GetRegularResponse> storeList;
}
