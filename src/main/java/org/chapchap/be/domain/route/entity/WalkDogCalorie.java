package org.chapchap.be.domain.route.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "walk_dog_calorie",
        indexes = {
                @Index(name = "idx_walkdog_walkroute", columnList = "walk_route_id"),
                @Index(name = "idx_walkdog_dogid", columnList = "dog_id")
        })
public class WalkDogCalorie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 헤더 참조
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "walk_route_id")
    private WalkRoute walkRoute;

    // 참조용으로 남겨두는 스냅샷 정보
    @Column(name = "dog_id")
    private Long dogId;           // 실제 Dog PK (nullable 허용 가능)

    @Column(length = 100)
    private String dogName;

    private Integer ageMonths;
    private Double weightKg;

    @Column(nullable = false)
    private int dailyCaloriesKcal;

    @Column(nullable = false)
    private int walkCaloriesKcal;

    /* 양방향 편의 메서드 */
    public void setWalkRoute(WalkRoute walkRoute) {
        this.walkRoute = walkRoute;
    }
}