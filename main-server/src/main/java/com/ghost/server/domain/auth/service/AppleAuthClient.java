package com.ghost.server.domain.auth.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.domain.user.entity.SocialProvider;
import org.springframework.stereotype.Component;

// TODO: 실제 Apple identity token JWT 검증으로 교체. 현재는 stub.
@Component
public class AppleAuthClient implements SocialAuthClient {

    @Override
    public SocialProvider provider() {
        return SocialProvider.APPLE;
    }

    @Override
    public SocialAuthInfo verify(String socialAccessToken) {
        if (socialAccessToken == null || socialAccessToken.isBlank()) {
            throw new BusinessException(ErrorCode.SOCIAL_TOKEN_INVALID);
        }
        return new SocialAuthInfo(
                SocialProvider.APPLE,
                "apple_" + socialAccessToken,
                "apple-" + socialAccessToken,
                null
        );
    }
}
