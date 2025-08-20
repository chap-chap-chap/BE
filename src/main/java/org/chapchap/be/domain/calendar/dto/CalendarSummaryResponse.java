package org.chapchap.be.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarSummaryResponse {

    @Schema(example = "2025-08-19")
    private LocalDate date;

    @Schema(example = "1500", description = "총 거리(m)")
    private Long distanceMeters;

    @Schema(example = "1200", description = "총 시간(초)")
    private Long durationSeconds;

    @Schema(example = "120", description = "사람 산책 칼로리(kcal)")
    private Integer humanWalkCaloriesKcal;

    @Schema(description = "반려견별 칼로리 목록 (이름-칼로리)")
    private List<DogCalorieDto> dogs;

    @Schema(example = "초코-45, 보리-60", description = "이름-칼로리 요약 문자열")
    private String dogsSummary;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DogCalorieDto {
        @Schema(example = "초코")
        private String name;

        @Schema(example = "45")
        private Integer dogWalkCaloriesKcal;
    }
}