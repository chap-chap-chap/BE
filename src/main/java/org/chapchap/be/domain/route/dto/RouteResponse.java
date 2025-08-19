package org.chapchap.be.domain.route.dto;

import java.util.List;

public record RouteResponse(
        int distanceMeters,         // 산책 경로 거리 (미터 단위)
        long durationSeconds,       // 예상 소요 시간 (초 단위, 도보 기준)
        String encodedPolyline,     // 경로를 나타내는 Google Polyline 인코딩 문자열
        int humanDailyCaloriesKcal, // 사람의 하루 권장 소모 칼로리
        int humanWalkCaloriesKcal,  // 사람의 이번 산책으로 소모되는 칼로리
        List<DogCalorie> dogs // 여러 마리 결과
) {
    public record DogCalorie(
            Long dogId,
            String name,
            Integer ageMonths,
            Double weightKg,
            int dogDailyCaloriesKcal,   // 강아지의 하루 권장 소모 칼로리
            int dogWalkCaloriesKcal        // 강아지의 이번 산책으로 소모되는 칼로리
    ) {}
}
