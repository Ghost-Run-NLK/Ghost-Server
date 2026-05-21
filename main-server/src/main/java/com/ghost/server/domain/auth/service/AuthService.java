package com.ghost.server.domain.auth.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.common.security.JwtTokenProvider;
import com.ghost.server.domain.auth.dto.AuthUserDto;
import com.ghost.server.domain.auth.dto.LoginResponse;
import com.ghost.server.domain.auth.dto.SocialLoginRequest;
import com.ghost.server.domain.user.entity.SocialProvider;
import com.ghost.server.domain.user.entity.User;
import com.ghost.server.domain.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final Map<SocialProvider, SocialAuthClient> clients;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public AuthService(List<SocialAuthClient> socialAuthClients,
                       UserService userService,
                       JwtTokenProvider tokenProvider) {
        this.clients = new EnumMap<>(SocialProvider.class);
        for (SocialAuthClient client : socialAuthClients) {
            this.clients.put(client.provider(), client);
        }
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse login(String providerParam, SocialLoginRequest request) {
        SocialProvider provider = parseProvider(providerParam);
        SocialAuthClient client = clients.get(provider);
        if (client == null) {
            throw new BusinessException(ErrorCode.SOCIAL_TOKEN_INVALID);
        }

        SocialAuthInfo info = client.verify(request.socialAccessToken());
        User user = userService.lookupOrCreate(
                info.provider(),
                info.socialId(),
                info.nickname(),
                info.avatarUrl()
        );
        String token = tokenProvider.issue(user.getId());
        return new LoginResponse(token, AuthUserDto.from(user));
    }

    private static SocialProvider parseProvider(String param) {
        if (param == null) {
            throw new BusinessException(ErrorCode.SOCIAL_TOKEN_INVALID);
        }
        try {
            return SocialProvider.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.SOCIAL_TOKEN_INVALID);
        }
    }
}
