package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.user.*;
import com.example.mocu.Exception.UserException;
import com.example.mocu.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public BaseResponse<List<GetUserResponse>> getUsersV1(
            @RequestParam(name = "nickname", required = false, defaultValue = "") String nickname,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "status", required = false, defaultValue = "active") String status) {
        log.info("[UserController.getUsersV1]");
        if (!status.equals("active") && !status.equals("dormant") && !status.equals("deleted")) {
            throw new UserException(INVALID_USER_STATUS);
        }

        List<GetUserResponse> result = userService.getUsersV1(nickname, email, status);

        if (result.isEmpty()) {
            throw new UserException(USER_NOT_FOUND);
        }

        return new BaseResponse<>(result);
    }

    @GetMapping("/v2")
    public ResponseEntity<List<GetUserResponse>> getUsersV2(
            @RequestParam(name = "nickname", required = false, defaultValue = "") String nickname,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "status", required = false, defaultValue = "") String status
    ) {
        log.info("[UserController.getUsersV2]");

        if (!status.equals("active") && !status.equals("dormant") && !status.equals("deleted")) {
            throw new UserException(INVALID_USER_STATUS);
        }

        List<GetUserResponse> result = userService.getUsersV2(nickname, email, status);

        return ResponseEntity.ok(result);
    }

    /**
     * my page 조회
     */
    @GetMapping("/{userId}/mypage")
    public BaseResponse<GetMyPageResponse> getMypageV1(@PathVariable Long userId) {
        log.info("[UserController.getMypage] - userId: {}", userId);
        //TODO: userID 검증 로직
        if (isUserIdCorrect(userId)) {
            throw new UserException(USER_NOT_FOUND);
        }

        return new BaseResponse<>(userService.getMypageV1(userId));
    }

    @GetMapping("/{userId}/mypage")
    public ResponseEntity<GetMyPageResponse> getMypageV2(@PathVariable Long userId) {
        log.info("[UserController.getMypageV2] - userId: {}", userId);
        if (isUserIdCorrect(userId)) {
            throw new UserException(USER_NOT_FOUND);
        }

        return ResponseEntity.ok(userService.getMypageV2(userId));
    }

    /**
     * 단골 설정 요청 처리
     * 단골 설청 ok -> status = "accept"
     * 단골 설정 no -> status = "request"
     * -> OK
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
    public BaseResponse<GetMyStoreListResponse> getMyStoreList(@PathVariable(name = "userId") long userId,
                                                               @RequestParam(name = "category", required = false) String category,
                                                               @RequestParam(name = "sort", required = false, defaultValue = "최신순") String sort,
                                                               @RequestParam(name = "isEventTrue", defaultValue = "false") boolean isEventTrue,
                                                               @RequestParam(name = "isCouponUsable", defaultValue = "false") boolean isCouponUsable,
                                                               @RequestParam(name = "userLatitude") double userLatitude,
                                                               @RequestParam(name = "userLongitude") double userLongitude,
                                                               @RequestParam(name = "page", defaultValue = "0") int page) {
        log.info("[UserController.getMyStoreList]");

        if (isUserIdCorrect(userId)) {
            throw new UserException(USER_NOT_FOUND);
        }

        return new BaseResponse<>(userService.getMyStoreList(userId, category, sort, isEventTrue, isCouponUsable, userLatitude, userLongitude, page));
    }

    /**
     * 단골로 설정 가능한 가게 목록 조회
     * 무한 스크롤 구현
     * -> OK
     */
    @GetMapping("/my-storelist/add-new/userId={userId}")
    public BaseResponse<List<GetStoreCanBeRegularResponse>> getStoreCanBeRegularList(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "userLatitude") double userLatitude,
            @RequestParam(name = "userLongitude") double userLongitude,
            @RequestParam(name = "page", defaultValue = "0") int page){
        log.info("[UserController.getStoreCanBeRegularList]");

        return new BaseResponse<>(userService.getStoreCanBeRegularList(userId, userLatitude, userLongitude, page));
    }

    /**
     * 단골로 설정 가능한 가게 목록 페이지에서 삭제
     * -> status = "not-accept" 로 변경
     * -> OK
     */
    @PatchMapping("/my-storelist/add-new/delete")
    public BaseResponse<String> updateRegularStatusToNotAccept(@RequestBody PatchUserRegularToNotAcceptRequest patchUserRegularToNotAcceptRequest){
        log.info("[UserController.updateRegularStatusToNotAccept]");

        userService.updateRegularStatusToNotAccept(patchUserRegularToNotAcceptRequest);

        return new BaseResponse<>("단골로 설정 가능한 목록에서 삭제가 완료되었습니다.");
    }
  
    public boolean isUserIdCorrect(Long userId) {
        log.info("[UserController.isUserIdCorrect]");

        int userIdCount = userService.getUserIdCount(userId);

        return userIdCount == 1;
    }
}
