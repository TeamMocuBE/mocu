package com.example.mocu.Service;

import com.example.mocu.Dao.ReviewDao;
import com.example.mocu.Dto.PostReviewRequest;
import com.example.mocu.Dto.PostReviewResponse;
import com.example.mocu.Exception.ReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
         * 해당 user가 해당 가게에 적립을 했는지, content 길이가 20자 이상인지 검사
         */
        validateReview(postReviewReqeust.getStoreId(), postReviewReqeust.getUserId(), postReviewReqeust.getContent());

        long reviewId = reviewDao.createReview(postReviewReqeust);
        return new PostReviewResponse(reviewId);
    }

    private void validateReview(long storeId, long userId, String content) {
        // TODO 1. 해당 user가 해당 가게에 적립을 했는지 검사
        if(reviewDao.isNotStamped(storeId, userId)){
            throw new ReviewException(IS_NOT_STAMPED);
        }

        // TODO 2. 리뷰 글자수가 20자 이상인지 검사
        if(reviewDao.isOver)
    }


}
