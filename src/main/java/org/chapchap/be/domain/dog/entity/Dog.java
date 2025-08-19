package org.chapchap.be.domain.dog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chapchap.be.domain.user.entity.User;

// Dog: 반려견 (1:N)
@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "dog")
public class Dog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 소유자
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable=false)
    private String name;

    private String breed;              // 품종
    private Double weightKg;           // 현재 체중
    private Integer ageMonths;         // 개월수

    private Boolean archived = false;  // 삭제 대신 보관 처리(이력 유지)
}
