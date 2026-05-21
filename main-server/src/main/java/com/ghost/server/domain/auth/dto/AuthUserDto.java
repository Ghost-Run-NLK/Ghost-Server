package com.ghost.server.domain.auth.dto;

import com.ghost.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답의 유저 정보")
public record AuthUserDto(
        @Schema(description = "유저 ID", example = "user_001") String userId,
        @Schema(description = "닉네임", example = "달리기 장인") String nickname,
        @Schema(description = "아바타 URL", example = "https://example.com/a.png", nullable = true) String avatarUrl
) {
    public static AuthUserDto from(User user) {
        return new AuthUserDto(
                "user_" + user.getId(),
                user.getNickname(),
                user.getAvatarUrl()
        );
    }
}
