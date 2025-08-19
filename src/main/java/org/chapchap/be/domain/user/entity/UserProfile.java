package org.chapchap.be.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

// UserProfile: 사람 프로필 (1:1)
@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "user_profile")
public class UserProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // User와 동일 PK

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    private Double humanWeightKg;   // 체중(kg)
    private Integer humanHeightCm;  // 키(cm)
    private Integer humanAge;       // 나이(만)

    @Enumerated(EnumType.STRING)
    private Sex humanSex; // MALE/FEMALE

    public enum Sex { MALE, FEMALE }
}
