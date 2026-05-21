package com.ghost.server.domain.user.service;

import com.ghost.server.domain.user.entity.SocialProvider;
import com.ghost.server.domain.user.entity.User;
import com.ghost.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User lookupOrCreate(SocialProvider provider, String socialId,
                               String nickname, String avatarUrl) {
        return userRepository.findBySocialProviderAndSocialId(provider, socialId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialProvider(provider)
                                .socialId(socialId)
                                .nickname(nickname)
                                .avatarUrl(avatarUrl)
                                .build()
                ));
    }
}
