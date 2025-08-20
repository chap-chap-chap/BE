package org.chapchap.be.domain.calendar.repository;

import org.chapchap.be.domain.calendar.entity.PhotoMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoMemoRepository extends JpaRepository<PhotoMemo, Long> {
}