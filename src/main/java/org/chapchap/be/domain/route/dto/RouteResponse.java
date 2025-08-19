package org.chapchap.be.domain.route.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "산책 경로 응답 DTO")
public record RouteResponse(

        @Schema(description = "산책 경로 거리 (미터 단위)", example = "1500")
        int distanceMeters,

        @Schema(description = "예상 소요 시간 (초 단위, 도보 기준)", example = "1200")
        long durationSeconds,

        @Schema(description = "경로를 나타내는 Google Polyline 인코딩 문자열", example = "abcdEFGH1234")
        String encodedPolyline,

        @Schema(description = "사람의 하루 권장 소모 칼로리", example = "2200")
        int humanDailyCaloriesKcal,

        @Schema(description = "사람의 이번 산책으로 소모되는 칼로리", example = "120")
        int humanWalkCaloriesKcal,

        @Schema(description = "강아지별 칼로리 정보 리스트")
        List<DogCalorie> dogs
) {
    @Schema(description = "강아지 칼로리 정보 DTO")
    public record DogCalorie(

            @Schema(description = "강아지 ID", example = "1")
            Long dogId,

            @Schema(description = "강아지 이름", example = "초코")
            String name,

            @Schema(description = "강아지 개월 수", example = "24")
            Integer ageMonths,

            @Schema(description = "강아지 몸무게(kg)", example = "5.5")
            Double weightKg,

            @Schema(description = "강아지 하루 권장 소모 칼로리", example = "350")
            int dogDailyCaloriesKcal,

            @Schema(description = "강아지 이번 산책으로 소모되는 칼로리", example = "45")
            int dogWalkCaloriesKcal
    ) {}
}
