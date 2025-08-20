package org.chapchap.be.domain.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CalendarResponse {

    private Long id;
    private LocalDate date;
    private Long distanceMeters;
    private Long durationSeconds;
    private Integer humanWalkCaloriesKcal;
    private Integer dogTotalCaloriesKcal;
    private String memo;
    private List<String> photoUrls; // 사진 경로 리스트
}
