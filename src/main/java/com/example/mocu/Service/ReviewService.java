package com.example.mocu.Service;

import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.ReviewDao;
import com.example.mocu.Dto.review.GetAvailableReviewCountResponse;
import com.example.mocu.Dto.review.PatchReviewReportToTrueRequest;
import com.example.mocu.Dto.review.PostReviewRequest;
import com.example.mocu.Dto.review.PostReviewResponse;
import com.example.mocu.Exception.ReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.mocu.Common.response.status.BaseResponseStatus.INVALID_REVIEW_LENGTH;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final MissionDao missionDao;

    public PostReviewResponse register(PostReviewRequest postReviewRequest) {
        log.info("[ReviewService.createReview]");

        // TODO 1. 리뷰등록 가능여부 검사
        validateReview(postReviewRequest);

        // TODO 2. 리뷰 등록
        long reviewId = reviewDao.createReview(postReviewRequest);

        // TODO 3. '리뷰 작성하기' 가 오늘의 미션에 해당되는지 체크
        // 오늘의 미션 중 '리뷰 작성하기' 가 있는지 체크
        boolean isTodayMission = false;
        if(missionDao.isReviewAssignedTodayMission(postReviewRequest.getUserId())){
            isTodayMission = true;
        }

        // TODO 4. 오늘의 미션 중 몇개를 수행했는지를 체크
        // 이미 오늘의 미션중 2개를 수행했으면, 추가로 오늘의 미션을 수행하더라도 스탬프 적립 X
        boolean isMissionComplete = false;
        int todayMissionPerformed = missionDao.getTodayMissionPerformed(postReviewRequest.getUserId());

        // TODO 5. todo 3, todo 4 모두 통과할 경우 '미션 완료' 처리
        if(isTodayMission && todayMissionPerformed < 2){
            isMissionComplete = true;
            missionDao.updateTodayMissionToDone(postReviewRequest.getUserId());
        }

        // TODO 6. return 형식 맞추기
        return new PostReviewResponse(reviewId, isTodayMission, isMissionComplete);
    }

    private void validateReview(PostReviewRequest postReviewRequest) {
        long storeId = postReviewRequest.getStoreId();
        long userId = postReviewRequest.getUserId();
        String content = postReviewRequest.getContent();

        // 리뷰 글자수가 10자 이상인지 검사
        if(content.length() < 10){
            throw new ReviewException(INVALID_REVIEW_LENGTH);
        }
    }

    public GetAvailableReviewCountResponse getAvailableReviewCount(Long userId) {
        log.info("[ReviewService.getAvailableReviewCount]");

        return reviewDao.getAvailableReviewCount(userId);
    }

    // 신고하기 처리
    public void updateReviewReportToTrue(PatchReviewReportToTrueRequest patchReviewReportToTrueRequest) {
        log.info("[ReviewService.updateReviewReportToTrue]");

        reviewDao.updateReviewReportToTrue(patchReviewReportToTrueRequest);
    }
}
