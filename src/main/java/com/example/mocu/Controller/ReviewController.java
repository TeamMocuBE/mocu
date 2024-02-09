package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.review.*;
import com.example.mocu.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 등록
     * + 가게 평점 update
     */
    @PostMapping("")
    public BaseResponse<PostReviewResponse> register(@Validated @RequestBody PostReviewRequest postReviewRequest) {
        log.info("[ReviewController.signUp]");

        return new BaseResponse<>(reviewService.register(postReviewRequest));
    }

    /**
     * 작성 가능 리뷰 (새 리뷰 쓰기)
     */
    @GetMapping("/available-review/{userId}")
    public BaseResponse<List<GetAvailableReviewResponse>> getAvailableReview(@PathVariable Long userId) {
        log.info("[ReviewController.getAvailabilityReview");

        return new BaseResponse<>(reviewService.getAvailableReview(userId));
    }

    /**
     * 리뷰 신고 처리
     */
    @PatchMapping("/update/report-true")
    public BaseResponse<String> updateReviewReportToTrue(@Validated @RequestBody PatchReviewReportToTrueRequest patchReviewReportToTrueRequest) {
        log.info("[ReviewController.updateReviewReportToTrue]");

        try {
            reviewService.updateReviewReportToTrue(patchReviewReportToTrueRequest);
            return new BaseResponse<>("신고처리 되었습니다.");
        } catch (RuntimeException e) {
            return new BaseResponse<>("신고처리 과정 중 오류 발생");
        }
    }

    /**
     * 내가 작성한 리뷰
     */
    @GetMapping("/{userId}/my-review")
    public BaseResponse<List<GetMyReviewResponse>> getMyReview(@PathVariable Long userId, @RequestParam(defaultValue = "최신순") String sort) {
        log.info(("ReviewController.getMyReview]"));

        return new BaseResponse<>(reviewService.getMyReview(userId, sort));
    }
}