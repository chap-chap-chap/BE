package org.chapchap.be.domain.calendar.entity;

import jakarta.persistence.*;
import lombok.*;
import org.chapchap.be.domain.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendar")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    // 집계값
    @Column(name = "distance_meters")
    private Long distanceMeters;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "human_walk_calories_kcal")
    private Integer humanWalkCaloriesKcal;

    @Column(name = "dog_total_calories_kcal")
    private Integer dogTotalCaloriesKcal;

    // 단일 메모
    @Column(columnDefinition = "TEXT")
    private String memo;

    // 소유자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 사진들
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PhotoMemo> photoMemos = new ArrayList<>();

    public void addPhotoMemo(PhotoMemo photoMemo) {
        if (photoMemo == null) return;
        photoMemo.setCalendar(this);
        this.photoMemos.add(photoMemo);
    }
}