package com.example.mocu.Service;

import com.example.mocu.Dao.ReviewDao;
import com.example.mocu.Dto.review.GetAvailableReviewCountResponse;
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
    public PostReviewResponse register(PostReviewRequest postReviewReqeust) {
        log.info("[ReviewService.createReview]");

        // TODO 1. 리뷰등록 가능여부 검사
        validateReview(postReviewReqeust);

        // TODO 2. '리뷰 작성하기' 가 오늘의 미션에 해당되는지 체크


        // TODO 3. 오늘의 미션 스탬프가 적립되었는지 체크


        // TODO 4. todo 2, todo 3 모두 통과할 경우 오늘의 미션 스탬프 1개 적립


        long reviewId = reviewDao.createReview(postReviewReqeust);
        return new PostReviewResponse(reviewId);
    }

    private void validateReview(PostReviewRequest postReviewReqeust) {
        long storeId = postReviewReqeust.getStoreId();
        long userId = postReviewReqeust.getUserId();
        String content = postReviewReqeust.getContent();

        // 리뷰 글자수가 10자 이상인지 검사
        if(content.length() < 10){
            throw new ReviewException(INVALID_REVIEW_LENGTH);
        }
    }

    public GetAvailableReviewCountResponse getAvailableReviewCount(Long userId) {
        log.info("[ReviewService.getAvailableReviewCount]");

        return reviewDao.getAvailableReviewCount(userId);
    }
}
