package com.ghost.server.domain.user.entity;

import com.ghost.server.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_social",
                columnNames = {"social_provider", "social_id"}
        )
)
public class User extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider", nullable = false, length = 16)
    private SocialProvider socialProvider;

    @Column(name = "social_id", nullable = false, length = 128)
    private String socialId;

    @Column(nullable = false, length = 32)
    private String nickname;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Builder
    private User(SocialProvider socialProvider, String socialId, String nickname, String avatarUrl) {
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }
}
