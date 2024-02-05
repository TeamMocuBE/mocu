package com.example.mocu.Service;

import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.ReviewDao;
import com.example.mocu.Dto.mission.IsTodayMission;
import com.example.mocu.Dto.review.*;
import com.example.mocu.Exception.ReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        List<IsTodayMission> todayMissionList = new ArrayList<>();
        if(missionDao.isTodayMissionAssigned(postReviewRequest.getUserId(), "리뷰 작성하기")){
            IsTodayMission todayMission = new IsTodayMission("리뷰 작성하기", true);
            todayMissionList.add(todayMission);

            // 1. get '리뷰 작성하기' 의 todayMissionId
            long todayMissionId = missionDao.getTodayMissionId(postReviewRequest.getUserId(), "리뷰 작성하기");
            // 2. 해당 todayMissionId 를 '미션 완료' 처리
            missionDao.updateTodayMissionToDone(todayMissionId);
        }

        // TODO 4. return
        return new PostReviewResponse(reviewId, todayMissionList);
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

    public List<GetAvailableReviewResponse> getAvailableReview(Long userId) {
        log.info("[ReviewService.getAvailableReviewCount]");

        return reviewDao.getAvailableReview(userId);
    }

    // 신고하기 처리
    public void updateReviewReportToTrue(PatchReviewReportToTrueRequest patchReviewReportToTrueRequest) {
        log.info("[ReviewService.updateReviewReportToTrue]");

        reviewDao.updateReviewReportToTrue(patchReviewReportToTrueRequest);
    }

    public List<GetMyReviewResponse> getMyReview(Long userId, String sort) {
        log.info("ReviewService.GeyMyReview");

        return reviewDao.getMyReview(userId, sort);
    }
}
