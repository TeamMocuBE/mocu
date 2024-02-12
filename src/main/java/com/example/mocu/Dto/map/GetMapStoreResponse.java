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
    private double latitude;
    private double longitude;
    private String category;
    private boolean hasEvent;
    private boolean isDueDate;
}
