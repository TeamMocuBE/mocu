package com.example.mocu.Dto.store;

import com.example.mocu.Dto.search.Search;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStoreSearchResponse {
    // 최근 검색어 list, 최근 방문한 가게 list, 이벤트 진행 중인 가게 list
    // 쿠폰 사용 임박 가게 list, 해당 유저를 위한 맞춤 가게 list
    private List<Search> recentSearches;
    private List<RecentlyVisitedStoreInfo> recentlyVisitedStoreInfoList;
    private List<StoreInEventInfo> storeInEventInfoList;
    private List<DueDateStoreInfo> dueDateStoreInfoList;
    private List<RecommendStoreInfo> recommendStoreInfoList;
}
