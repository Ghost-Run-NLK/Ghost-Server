package com.ghost.server.domain.auth.service;

import com.ghost.server.domain.user.entity.SocialProvider;

public interface SocialAuthClient {

    SocialProvider provider();

    SocialAuthInfo verify(String socialAccessToken);
}
