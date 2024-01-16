package com.example.mocu.Service;

import com.example.mocu.Dao.ReviewDao;
import com.example.mocu.Dto.review.PostReviewRequest;
import com.example.mocu.Dto.review.PostReviewResponse;
import com.example.mocu.Exception.ReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.mocu.Common.response.status.BaseResponseStatus.INVALID_REVIEW_LENGTH;
import static com.example.mocu.Common.response.status.BaseResponseStatus.IS_NOT_STAMPED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    public PostReviewResponse register(PostReviewRequest postReviewReqeust) {
        log.info("[ReviewService.createReview]");

        /**
         * TODO : 리뷰등록 가능여부 검사
         */
        validateReview(postReviewReqeust);

        long reviewId = reviewDao.createReview(postReviewReqeust);
        return new PostReviewResponse(reviewId);
    }

    private void validateReview(PostReviewRequest postReviewReqeust) {
        long storeId = postReviewReqeust.getStoreId();
        long userId = postReviewReqeust.getUserId();
        String content = postReviewReqeust.getContent();

        // TODO 1. 리뷰 글자수가 10자 이상인지 검사
        if(content.length() < 10){
            throw new ReviewException(INVALID_REVIEW_LENGTH);
        }
    }

}
