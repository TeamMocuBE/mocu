package com.example.mocu.Controller;

import com.example.mocu.Common.response.BaseResponse;
import com.example.mocu.Dto.user.GetMyPageResponse;
import com.example.mocu.Dto.user.GetUserResponse;
import com.example.mocu.Exception.UserException;
import com.example.mocu.Service.UserService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.mocu.Common.response.status.BaseResponseStatus.INVALID_USER_STATUS;

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
        return new BaseResponse<>(userService.getUsers(nickname, email, status));
    }


    @GetMapping("/{userId}/mypage")
    public BaseResponse<GetMyPageResponse> getMypage(@PathVariable Long userId) {
        log.info("[UserController.getMypage] - userId: {}", userId);
        //TODO: userID 검증 로직

        return new BaseResponse<>(userService.getMypage(userId));
    }

    /**
     * 단골 설정 요청 처리
     */
    /*
    @PostMapping("/regular-request")
    public BaseResponse<PostUserRegularResponse> createRegular
    */


    /**
     * 가게 검색 페이지의 추천 가게 목록 조회
     */


}
