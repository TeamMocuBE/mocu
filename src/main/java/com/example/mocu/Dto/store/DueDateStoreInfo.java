package com.example.mocu.Dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DueDateStoreInfo {
    private int numOfStamp;
    private int maxStamp;
    private String storeName;
    private String coordinate;
}
