package org.chapchap.be.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.chapchap.be.domain.dog.entity.Dog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// User: 인증/계정
@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // USER/ADMIN

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = true)
    private UserProfile profile;

    // 강아지 컬렉션은 필요 시 LAZY로 접근
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Dog> dogs = new ArrayList<>();

    private LocalDateTime lastLoginAt;

    public enum Role { USER, ADMIN }
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
    public void changePassword(String encoded) {
        this.password = encoded;
    }
}
