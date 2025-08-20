package org.chapchap.be.domain.calendar.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photo_memo")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;

    // 호환을 위해 남겨두지만 현재는 단일 메모를 Calendar.memo에 저장
    @Column(columnDefinition = "TEXT")
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;
}