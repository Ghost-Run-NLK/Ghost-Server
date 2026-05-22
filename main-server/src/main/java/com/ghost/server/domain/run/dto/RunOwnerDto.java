package com.ghost.server.domain.run.dto;

import com.ghost.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "러닝 기록 소유자 정보")
public record RunOwnerDto(
        @Schema(description = "유저 ID", example = "user_001") String userId,
        @Schema(description = "닉네임", example = "달리기 장인") String nickname,
        @Schema(description = "아바타 URL", example = "https://example.com/a.png", nullable = true) String avatarUrl
) {
    public static RunOwnerDto from(User user) {
        return new RunOwnerDto(
                "user_" + user.getId(),
                user.getNickname(),
                user.getAvatarUrl()
        );
    }
}
