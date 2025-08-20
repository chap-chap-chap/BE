package org.chapchap.be.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chapchap.be.domain.calendar.dto.CalendarSummaryResponse;
import org.chapchap.be.domain.calendar.entity.Calendar;
import org.chapchap.be.domain.calendar.entity.PhotoMemo;
import org.chapchap.be.domain.calendar.repository.CalendarRepository;
import org.chapchap.be.domain.calendar.repository.PhotoMemoRepository;
import org.chapchap.be.domain.calendar.repository.WalkAggregateDao;
import org.chapchap.be.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final PhotoMemoRepository photoMemoRepository;
    private final WalkAggregateDao walkAggregateDao;

    /**
     * 사진 여러 장 + 메모(단일) 저장
     * 주행거리/시간/칼로리는 WalkAggregateDao에서 조회하여 캘린더에 세팅
     */
    @Transactional
    public Calendar createCalendar(
            User user,
            LocalDate date,
            String memo,
            List<MultipartFile> photos
    ) throws IOException {

        // 날짜별 기존 캘린더가 있으면 업데이트, 없으면 생성
        Calendar calendar = calendarRepository.findByUserAndDate(user, date)
                .orElseGet(() -> Calendar.builder()
                        .user(user)
                        .date(date)
                        .build());

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

        // 단일 메모
        calendar.setMemo(memo != null ? memo : "");

        // 사진 저장(여러 장)
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile file : photos) {
                if (file.isEmpty()) continue;

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String absPath = "/app/uploads/" + fileName; // 컨테이너 내부 저장 경로
                File dest = new File(absPath);
                dest.getParentFile().mkdirs();
                file.transferTo(dest);

                // 접근 URL은 /uploads/파일명 (WebMvcConfig에서 매핑)
                String publicUrl = "/uploads/" + fileName;

                PhotoMemo photoMemo = PhotoMemo.builder()
                        .photoUrl(publicUrl)
                        .memo("") // (과거 호환용)
                        .calendar(calendar)
                        .build();

                calendar.addPhotoMemo(photoMemo);
            }
        }

        Calendar saved = calendarRepository.save(calendar);
        log.info("Calendar saved (id={}, userId={}, date={})", saved.getId(), user.getId(), date);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Calendar> getCalendarsByUser(User user) {
        return calendarRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public CalendarSummaryResponse getCalendarSummary(User user, LocalDate date) {
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