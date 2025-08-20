package org.chapchap.be.domain.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "캘린더 요약 응답")
public class CalendarSummaryResponse {

    @Schema(description = "날짜(yyyy-MM-dd)")
    private LocalDate date;

    @Schema(description = "주행 거리(미터)")
    private Long distanceMeters;

    @Schema(description = "주행 시간(초)")
    private Long durationSeconds;

    @Schema(description = "사람 산책 칼로리(kcal)")
    private Integer humanWalkCaloriesKcal;

    @Schema(description = "반려견별 칼로리")
    private List<DogCalorieDto> dogs;

    @Schema(description = "반려견 한 줄 요약 (이름-칼로리, ...)")
    private String dogsSummary;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DogCalorieDto {
        private String name;
        private Integer dogWalkCaloriesKcal;
    }
}
