package org.chapchap.be.domain.calendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.calendar.dto.CalendarResponse;
import org.chapchap.be.domain.calendar.dto.CalendarSummaryResponse;
import org.chapchap.be.domain.calendar.service.CalendarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar API", description = "캘린더 기록 관리")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "캘린더 생성 (사진 여러 장 + 메모 1개, 집계값은 DB에서 조회)")
    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public CalendarResponse createCalendar(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "memo", required = false) String memo,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos
    ) throws IOException {
        return calendarService.createCalendar(date, memo, photos);
    }

    @Operation(summary = "내 캘린더 목록 조회")
    @GetMapping("/list")
    public List<CalendarResponse> getMyCalendars() {
        return calendarService.getCalendarsByUser();
    }

    @Operation(summary = "요약 조회 (거리/시간/사람칼/반려견 칼로리들)")
    @GetMapping("/summary")
    public CalendarSummaryResponse getSummary(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return calendarService.getCalendarSummary(date);
    }
}
