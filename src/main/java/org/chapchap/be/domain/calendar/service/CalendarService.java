package org.chapchap.be.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chapchap.be.domain.calendar.dto.CalendarResponse;
import org.chapchap.be.domain.calendar.dto.CalendarSummaryResponse;
import org.chapchap.be.domain.calendar.entity.Calendar;
import org.chapchap.be.domain.calendar.repository.CalendarRepository;
import org.chapchap.be.domain.calendar.repository.PhotoMemoRepository;
import org.chapchap.be.domain.calendar.repository.WalkAggregateDao;
import org.chapchap.be.domain.calendar.util.CalendarMapper;
import org.chapchap.be.domain.user.entity.User;
import org.chapchap.be.domain.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.chapchap.be.domain.calendar.util.CalendarMapper.toResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;
    private final PhotoMemoRepository photoMemoRepository;
    private final WalkAggregateDao walkAggregateDao;

    /**
     * 사진 여러 장 + 메모(단일) 저장
     * 주행거리/시간/칼로리는 WalkAggregateDao에서 조회하여 캘린더에 세팅
     */
    @Transactional
    public CalendarResponse createCalendar(LocalDate date, String memo, List<MultipartFile> photos)
            throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Calendar calendar = calendarRepository.findByUserAndDate(user, date)
                .orElseGet(() -> Calendar.builder().user(user).date(date).build());

        // 집계값 조회 (walk_route / walk_dog_calorie)
        var coreOpt = walkAggregateDao.findCore(user.getId(), date);
        var dogs = walkAggregateDao.findDogs(user.getId(), date);

        long distanceMeters = coreOpt.map(WalkAggregateDao.WalkCore::getDistanceMeters).orElse(0L);
        long durationSeconds = coreOpt.map(WalkAggregateDao.WalkCore::getDurationSeconds).orElse(0L);
        int humanKcal = coreOpt.map(WalkAggregateDao.WalkCore::getHumanWalkCaloriesKcal).orElse(0);
        int dogTotal = dogs.stream().mapToInt(WalkAggregateDao.DogCalorie::getDogWalkCaloriesKcal).sum();

        calendar.setDistanceMeters(distanceMeters);
        calendar.setDurationSeconds(durationSeconds);
        calendar.setHumanWalkCaloriesKcal(humanKcal);
        calendar.setDogTotalCaloriesKcal(dogTotal);
        calendar.setMemo(memo != null ? memo : "");

        Calendar saved = calendarRepository.save(calendar);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CalendarResponse> getCalendarsByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return calendarRepository.findByUser(user).stream()
                .map(CalendarMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CalendarSummaryResponse getCalendarSummary(LocalDate date) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        var coreOpt = walkAggregateDao.findCore(user.getId(), date);
        var dogRows = walkAggregateDao.findDogs(user.getId(), date);

        Long distanceMeters = coreOpt.map(WalkAggregateDao.WalkCore::getDistanceMeters).orElse(0L);
        Long durationSeconds = coreOpt.map(WalkAggregateDao.WalkCore::getDurationSeconds).orElse(0L);
        Integer humanWalkCaloriesKcal = coreOpt.map(WalkAggregateDao.WalkCore::getHumanWalkCaloriesKcal).orElse(0);

        var dogs = dogRows.stream()
                .map(d -> CalendarSummaryResponse.DogCalorieDto.builder()
                        .name(d.getName())
                        .dogWalkCaloriesKcal(d.getDogWalkCaloriesKcal())
                        .build())
                .collect(Collectors.toList());

        String dogsSummary = dogs.stream()
                .map(d -> d.getName() + "-" + d.getDogWalkCaloriesKcal())
                .collect(Collectors.joining(", "));

        return CalendarSummaryResponse.builder()
                .date(date)
                .distanceMeters(distanceMeters)
                .durationSeconds(durationSeconds)
                .humanWalkCaloriesKcal(humanWalkCaloriesKcal)
                .dogs(dogs)
                .dogsSummary(dogsSummary)
                .build();
    }
}
