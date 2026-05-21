package com.ghost.server.domain.auth.service;

import com.ghost.server.domain.user.entity.SocialProvider;

public record SocialAuthInfo(
        SocialProvider provider,
        String socialId,
        String nickname,
        String avatarUrl
) {
}
