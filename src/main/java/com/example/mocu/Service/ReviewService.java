package com.example.mocu.Service;

import com.example.mocu.Dao.MissionDao;
import com.example.mocu.Dao.ReviewDao;
import com.example.mocu.Dao.StoreDao;
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
    private final StoreDao storeDao;

    public PostReviewResponse registerReview(PostReviewRequest postReviewRequest) {
        log.info("[ReviewService.createReview]");

        /**
         * 1. 리뷰 작성 X -> status = '작성이전'
         * reviewId만 생성
         */
        if(postReviewRequest.getContent() == null){
            int rate = 0;
            String content = "";
            // TODO 1. reviewId 생성
            long reviewId = reviewDao.createReviewId(
                    postReviewRequest.getUserId(),
                    postReviewRequest.getStoreId(),
                    rate,
                    content,
                    "작성이전");
            List<IsTodayMission> todayMissionList = new ArrayList<>();

            return new PostReviewResponse(reviewId, todayMissionList);
        }

        /**
         * 2. 리뷰 작성 O -> status = '작성이후'
         */

        // TODO 1. reviewId 생성
        long reviewId = reviewDao.createReviewId(
                postReviewRequest.getUserId(),
                postReviewRequest.getStoreId(),
                postReviewRequest.getRate(),
                postReviewRequest.getContent(),
                "작성이후");

        // TODO 2. '리뷰 작성하기' 가 오늘의 미션에 해당되는지 체크
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

        // TODO 3. 가게 평점 update
        storeDao.updateStoreRating(postReviewRequest.getStoreId(), postReviewRequest.getRate());

        // TODO 4. return
        return new PostReviewResponse(reviewId, todayMissionList);
    }

    public List<GetAvailableReviewResponse> getAvailableReview(Long userId) {
        log.info("[ReviewService.getAvailableReviewCount]");

        return reviewDao.getAvailableReview(userId);
    }

    /**
     * 신고하기 처리
     */
    public void updateReviewReportToTrue(PatchReviewReportToTrueRequest patchReviewReportToTrueRequest) {
        log.info("[ReviewService.updateReviewReportToTrue]");

        reviewDao.updateReviewReportToTrue(patchReviewReportToTrueRequest);
    }

    public List<GetMyReviewResponse> getMyReview(Long userId, String sort) {
        log.info("ReviewService.GeyMyReview");

        return reviewDao.getMyReview(userId, sort);
    }


    public PatchReviewResponse correctReview(PatchReviewRequest patchReviewRequest) {
        log.info("[ReviewService.correctReview]");

        // TODO 1. review update
        reviewDao.updateReview(patchReviewRequest);

        // TODO 2. '리뷰 작성하기' 가 오늘의 미션에 해당되는지 체크
        // 오늘의 미션 중 '리뷰 작성하기' 가 있는지 체크
        List<IsTodayMission> todayMissionList = new ArrayList<>();
        if(missionDao.isTodayMissionAssigned(patchReviewRequest.getUserId(), "리뷰 작성하기")){
            IsTodayMission todayMission = new IsTodayMission("리뷰 작성하기", true);
            todayMissionList.add(todayMission);

            // 1. get '리뷰 작성하기' 의 todayMissionId
            long todayMissionId = missionDao.getTodayMissionId(patchReviewRequest.getUserId(), "리뷰 작성하기");
            // 2. 해당 todayMissionId 를 '미션 완료' 처리
            missionDao.updateTodayMissionToDone(todayMissionId);
        }

        // TODO 3. 가게 평점 update
        storeDao.updateStoreRating(patchReviewRequest.getStoreId(), patchReviewRequest.getRate());

        // TODO 4. return
        return new PatchReviewResponse(todayMissionList);
    }


}
