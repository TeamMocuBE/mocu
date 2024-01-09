package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.PostReviewResponse;
import com.example.mocu.Dto.PostReviewRequest;
import com.example.mocu.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 등록
     */
    @PostMapping("")
    public BaseResponse<PostReviewResponse> register(@Validated @RequestBody PostReviewRequest postReviewReqeust){
        log.info("[ReviewController.signUp]");

        return new BaseResponse<>(reviewService.register(postReviewReqeust));
    }

}