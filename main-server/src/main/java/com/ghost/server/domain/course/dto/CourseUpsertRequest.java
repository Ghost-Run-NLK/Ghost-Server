package com.ghost.server.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@Schema(description = "코스 등록/수정 요청 (관리자)")
public record CourseUpsertRequest(
        @Schema(description = "코스 이름", example = "성성호수 공원", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,

        @Schema(description = "주소", example = "천안시 서북구 천안대로 1223-24", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String address,

        @Schema(description = "총 거리 (m)", example = "3500", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @PositiveOrZero Integer distance,

        @Schema(description = "출발지 위도", example = "36.8097", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double startLat,
        @Schema(description = "출발지 경도", example = "127.0079", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double startLng,

        @Schema(description = "도착지 위도", example = "36.8210", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double endLat,
        @Schema(description = "도착지 경도", example = "127.0190", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double endLng,

        @Schema(description = "경로 좌표 (입력 순서대로 sequence 부여, PUT 시 기존 전체 교체)",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Valid List<RoutePointDto> routePoints
) {
}
