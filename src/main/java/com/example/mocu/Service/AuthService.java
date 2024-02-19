package com.example.mocu.Service;

import com.example.mocu.Dao.OwnerDao;
import com.example.mocu.Dao.UserDao;
import com.example.mocu.Dto.Push.PostRegisterPushTokenRequest;
import com.example.mocu.Dto.owner.GetOwnerResponse;
import com.example.mocu.Dto.owner.PostOwnerRequest;
import com.example.mocu.Dto.user.AuthResponse;
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
    private final OwnerDao ownerDao;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final PushService pushService;

    public AuthResponse userLogin(OAuthLoginParams params) {
        log.info("[AuthService.userLogin]");
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long userId = findOrCreateUser(oAuthInfoResponse);

        AuthTokens authTokens = authTokensGenerator.generate(userId);


        // TODO 2. accessToken 으로 카카오에서 uuid 얻어와서 user table에 저장


        return new AuthResponse(userId, authTokens);
    }

    public AuthResponse ownerLogin(OAuthLoginParams params) {
        log.info("[AuthService.ownerLogin]");
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long ownerId = findOrCreateOwner(oAuthInfoResponse);

        AuthTokens authTokens = authTokensGenerator.generate(ownerId);


        // TODO 2. accessToken 으로 카카오에서 uuid 얻어와서 owner table에 저장



        return new AuthResponse(ownerId, authTokens);
    }

    private Long findOrCreateUser(OAuthInfoResponse oAuthInfoResponse) {
        log.info("[AuthService.findOrCreateUser]");
        //List<GetUserResponse> users = userDao.getUsers(oAuthInfoResponse.getName(), oAuthInfoResponse.getEmail(), "active");
        List<GetUserResponse> users = userDao.getUsers(oAuthInfoResponse.getName(), "active");
        if (!users.isEmpty()) {
            return users.get(0).getUserId();
        } else {
            return createUser(oAuthInfoResponse);
        }
    }

    private Long createUser(OAuthInfoResponse oAuthInfoResponse) {
        log.info("[AuthService.createUser]");
        PostUserRequest postUserRequest = new PostUserRequest();
        postUserRequest.setEmail(oAuthInfoResponse.getEmail());
        postUserRequest.setName(oAuthInfoResponse.getName());
        postUserRequest.setProvider(oAuthInfoResponse.getOAuthProvider().toString());
        postUserRequest.setName(oAuthInfoResponse.getName());
        postUserRequest.setProfileImage(oAuthInfoResponse.getProfileImageUrl());

        return userDao.createUser(postUserRequest);
    }

    private Long findOrCreateOwner(OAuthInfoResponse oAuthInfoResponse) {
        log.info("[AuthService.findOrCreateOwner]");
        //List<GetUserResponse> users = userDao.getUsers(oAuthInfoResponse.getName(), oAuthInfoResponse.getEmail(), "active");
        List<GetOwnerResponse> owners = userDao.getOwners(oAuthInfoResponse.getName(), "active");
        if (!owners.isEmpty()) {
            return owners.get(0).getOwnerId();
        } else {
            return createUser(oAuthInfoResponse);
        }
    }

    private Long createOwner(OAuthInfoResponse oAuthInfoResponse) {
        log.info("[AuthService.createOwner]");
        PostOwnerRequest postOwnerRequest = new PostOwnerRequest();
        postOwnerRequest.setEmail(oAuthInfoResponse.getEmail());
        postOwnerRequest.setName(oAuthInfoResponse.getName());
        postOwnerRequest.setProvider(oAuthInfoResponse.getOAuthProvider().toString());
        postOwnerRequest.setName(oAuthInfoResponse.getName());
        postOwnerRequest.setProfileImage(oAuthInfoResponse.getProfileImageUrl());

        return userDao.createOwner(postOwnerRequest);
    }

    public void registerPushToken(PostRegisterPushTokenRequest postRegisterPushTokenRequest) {


    }
}