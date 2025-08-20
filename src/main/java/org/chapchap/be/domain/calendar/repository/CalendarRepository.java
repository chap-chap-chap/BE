package org.chapchap.be.domain.calendar.repository;

import org.chapchap.be.domain.calendar.entity.Calendar;
import org.chapchap.be.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findByUser(User user);
    Optional<Calendar> findByUserAndDate(User user, LocalDate date);
}