package com.example.mocu.Service;

import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.user.GetUserResponse;
import com.example.mocu.Dto.user.PostUserRequest;
import com.example.mocu.auth.AuthTokens;
import com.example.mocu.auth.AuthTokensGenerator;
import com.example.mocu.socialLogin.infoResponse.OAuthInfoResponse;
import com.example.mocu.socialLogin.params.OAuthLoginParams;
import com.example.mocu.socialLogin.service.RequestOAuthInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public AuthTokens login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long userId = findOrCreateMember(oAuthInfoResponse);

        return authTokensGenerator.generate(userId);
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        List<GetUserResponse> users = userDao.getUsers(oAuthInfoResponse.getName(), oAuthInfoResponse.getEmail(), "active");
        if (!users.isEmpty()) {
            return users.get(0).getUserId();
        } else {
            return createUser(oAuthInfoResponse);
        }
    }

    private Long createUser(OAuthInfoResponse oAuthInfoResponse) {
        PostUserRequest postUserRequest = new PostUserRequest();
        postUserRequest.setEmail(oAuthInfoResponse.getEmail());
        postUserRequest.setName(oAuthInfoResponse.getName());
        postUserRequest.setProvider(oAuthInfoResponse.getOAuthProvider().toString());
        postUserRequest.setNickname(oAuthInfoResponse.getName());
        postUserRequest.setProfileImage(oAuthInfoResponse.getProfileImageUrl());

        return userDao.createUser(postUserRequest);
    }
}
