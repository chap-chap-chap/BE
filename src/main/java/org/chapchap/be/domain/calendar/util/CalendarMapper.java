package org.chapchap.be.domain.calendar.util;

import org.chapchap.be.domain.calendar.dto.CalendarResponse;
import org.chapchap.be.domain.calendar.entity.Calendar;
import org.chapchap.be.domain.calendar.entity.PhotoMemo;

public final class CalendarMapper {

    private CalendarMapper() {}

    public static CalendarResponse toResponse(Calendar c) {
        return CalendarResponse.builder()
                .id(c.getId())
                .date(c.getDate())
                .distanceMeters(c.getDistanceMeters())
                .durationSeconds(c.getDurationSeconds())
                .humanWalkCaloriesKcal(c.getHumanWalkCaloriesKcal())
                .dogTotalCaloriesKcal(c.getDogTotalCaloriesKcal())
                .memo(c.getMemo())
                .photoUrls(
                        c.getPhotoMemos() == null ? java.util.List.of()
                                : c.getPhotoMemos().stream()
                                .map(PhotoMemo::getPhotoUrl)
                                .toList()
                )
                .build();
    }
}
