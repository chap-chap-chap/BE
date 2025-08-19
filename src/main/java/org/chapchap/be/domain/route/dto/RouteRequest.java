package org.chapchap.be.domain.route.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "산책 경로 요청 DTO")
public record RouteRequest(

        @Schema(description = "출발지 좌표")
        Point origin,

        @Schema(description = "도착지 좌표")
        Point destination,

        @Schema(description = "선택된 강아지 ID 리스트", example = "[1, 2]")
        List<Long> dogIds   // 없으면 null 또는 빈 배열
) {
    @Schema(description = "좌표 정보 DTO")
    public record Point(
            @Schema(description = "위도", example = "37.5665")
            double latitude,

            @Schema(description = "경도", example = "126.9780")
            double longitude
    ) {}
}
