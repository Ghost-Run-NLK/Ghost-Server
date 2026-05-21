package com.ghost.server.domain.auth.service;

import com.ghost.server.common.exception.BusinessException;
import com.ghost.server.common.exception.ErrorCode;
import com.ghost.server.domain.user.entity.SocialProvider;
import org.springframework.stereotype.Component;

// TODO: 실제 Kakao /v2/user/me 호출로 교체. 현재는 stub — token을 그대로 socialId로 사용.
@Component
public class KakaoAuthClient implements SocialAuthClient {

    @Override
    public SocialProvider provider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public SocialAuthInfo verify(String socialAccessToken) {
        if (socialAccessToken == null || socialAccessToken.isBlank()) {
            throw new BusinessException(ErrorCode.SOCIAL_TOKEN_INVALID);
        }
        return new SocialAuthInfo(
                SocialProvider.KAKAO,
                "kakao_" + socialAccessToken,
                "kakao-" + socialAccessToken,
                null
        );
    }
}
