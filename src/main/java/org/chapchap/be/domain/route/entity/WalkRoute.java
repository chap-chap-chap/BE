package org.chapchap.be.domain.route.entity;

import jakarta.persistence.*;
import lombok.*;
import org.chapchap.be.domain.user.entity.User;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "walk_route")
public class WalkRoute {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소유자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    // 좌표 (더 정규화하고 싶으면 Embeddable로 빼도 OK)
    @Column(nullable = false)
    private double originLat;

    @Column(nullable = false)
    private double originLng;

    @Column(nullable = false)
    private double destLat;

    @Column(nullable = false)
    private double destLng;

    // ORS 결과
    @Column(nullable = false)
    private int distanceMeters;

    @Column(nullable = false)
    private long durationSeconds;

    @Lob // polyline 길이가 길 수 있으니
    @Column(nullable = false)
    private String encodedPolyline;

    // 사람 칼로리
    @Column(nullable = false)
    private int humanDailyCaloriesKcal;

    @Column(nullable = false)
    private int humanWalkCaloriesKcal;

    // 생성 시각
    @Builder.Default
    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // 디테일
    @Builder.Default
    @OneToMany(mappedBy = "walkRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkDogCalorie> dogCalories = new ArrayList<>();

    public void addDogCalorie(WalkDogCalorie dc) {
        dc.setWalkRoute(this);
        this.dogCalories.add(dc);
    }
}