package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.user.*;
import com.example.mocu.Exception.UserException;
import com.example.mocu.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * 회원 목록 조회
     */
    @GetMapping("")
    public BaseResponse<List<GetUserResponse>> getUsers(
            @RequestParam(name = "nickname", required = false, defaultValue = "") String nickname,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "status", required = false, defaultValue = "active") String status) {
        log.info("[UserController.getUsers]");
        if (!status.equals("active") && !status.equals("dormant") && !status.equals("deleted")) {
            throw new UserException(INVALID_USER_STATUS);
        }

        List<GetUserResponse> result = userService.getUsers(nickname, email, status);

        if (result.isEmpty()) {
            throw new UserException(USER_NOT_FOUND);
        }

        return new BaseResponse<>(result);
    }

    /**
     * my page 조회
     */
    @GetMapping("/{userId}/mypage")
    public BaseResponse<GetMyPageResponse> getMypage(@PathVariable Long userId) {
        log.info("[UserController.getMypage] - userId: {}", userId);
        //TODO: userID 검증 로직
        if (!isUserIdCorrect(userId)) {
            throw new UserException(USER_NOT_FOUND);
        }

        return new BaseResponse<>(userService.getMypage(userId));
    }

    /**
     * 단골 설정 요청 처리
     * 단골 설청 ok -> status = "accept"
     * 단골 설정 no -> status = "request"
     */
    @PatchMapping("/regular-request")
    public BaseResponse<PatchUserRegularResponse> handleRegularRequest(@RequestBody PatchUserRegularRequest patchUserRegularRequest) {
        log.info("[UserController.handleRegularRequest]");

        return new BaseResponse<>(userService.handleRegularRequest(patchUserRegularRequest));
    }

    /**
     * 단골 페이지 조회
     */
    @GetMapping("/{userId}/my-storelist")
    public BaseResponse<GetMyStoreListResponse> getMyStoreList(@PathVariable long userId,
                                                               @RequestParam(required = false) String category,
                                                               @RequestParam(required = false, defaultValue = "최신순") String sort,
                                                               @RequestParam(defaultValue = "false") boolean isEventTrue,
                                                               @RequestParam(defaultValue = "false") boolean isCouponUsable,
                                                               @RequestParam double userLatitude,
                                                               @RequestParam double userLongitude) {
        log.info("[UserController.getMyStoreList]");

        if (!isUserIdCorrect(userId)) {
            throw new UserException(USER_NOT_FOUND);
        }

        return new BaseResponse<>(userService.getMyStoreList(userId, category, sort, isEventTrue, isCouponUsable, userLatitude, userLongitude));
    }

    public boolean isUserIdCorrect(Long userId) {
        log.info("[UserController.isUserIdCorrect]");

        int userIdCount = userService.getUserIdCount(userId);

        return userIdCount == 1;
    }
}
