package com.ghost.server.domain.auth.controller;

import com.ghost.server.common.response.ApiResponse;
import com.ghost.server.domain.auth.dto.LoginResponse;
import com.ghost.server.domain.auth.dto.SocialLoginRequest;
import com.ghost.server.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Tag(name = "Auth", description = "소셜 로그인 API (데모 미사용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "소셜 로그인",
            description = "kakao / apple provider로 소셜 access token을 받아 검증 후 JWT 발급. 첫 로그인 시 유저 자동 생성. (현재 social client는 stub)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 (accessToken + user)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "지원하지 않는 provider 또는 토큰 비어있음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "SOCIAL_TOKEN_INVALID",
                                    value = "{\"code\": 400, \"message\": \"소셜 인증 토큰이 유효하지 않습니다\", \"data\": null}"
                            )
                    )
            )
    })
    @PostMapping("/{provider}")
    public LoginResponse login(
            @Parameter(description = "소셜 provider (kakao | apple)", example = "kakao", required = true)
            @PathVariable String provider,
            @Valid @RequestBody SocialLoginRequest request
    ) {
        return authService.login(provider, request);
    }
}
