package com.example.mocu.Dto.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetMapStoreResponse {
    private long storeId;
    private double storeLatitude;
    private double storeLongitude;
    private String category;
    private boolean hasEvent;
    private boolean isDueDate;
}
