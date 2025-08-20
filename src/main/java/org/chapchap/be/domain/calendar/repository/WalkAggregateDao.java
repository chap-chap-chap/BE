package org.chapchap.be.domain.calendar.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalkAggregateDao {

    private final JdbcTemplate jdbcTemplate;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalkCore {
        private long distanceMeters;
        private long durationSeconds;
        private int humanWalkCaloriesKcal;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DogCalorie {
        private String name;
        private int dogWalkCaloriesKcal;
    }

    /** created_at(Datetime) 하루 구간에서 가장 마지막(최신) 기록 1건 */
    public Optional<WalkCore> findCore(Long userId, LocalDate date) {
        var start = Timestamp.valueOf(date.atStartOfDay());
        var end   = Timestamp.valueOf(date.plusDays(1).atStartOfDay());

        String sql = """
            SELECT distance_meters           AS distanceMeters,
                   duration_seconds          AS durationSeconds,
                   human_walk_calories_kcal  AS humanWalkCaloriesKcal
            FROM walk_route
            WHERE owner_id = ?
              AND created_at >= ?
              AND created_at <  ?
            ORDER BY created_at DESC
            LIMIT 1
            """;

        var list = jdbcTemplate.query(sql, (rs, rn) -> mapWalkCore(rs), userId, start, end);
        return list.stream().findFirst();
    }

    /** 같은 날짜 구간의 반려견별 칼로리 목록 */
    public List<DogCalorie> findDogs(Long userId, LocalDate date) {
        var start = Timestamp.valueOf(date.atStartOfDay());
        var end   = Timestamp.valueOf(date.plusDays(1).atStartOfDay());

        String sql = """
            SELECT d.dog_name           AS name,
                   d.walk_calories_kcal AS dogWalkCaloriesKcal
            FROM walk_dog_calorie d
            JOIN walk_route r ON d.walk_route_id = r.id
            WHERE r.owner_id = ?
              AND r.created_at >= ?
              AND r.created_at <  ?
            ORDER BY d.dog_name
            """;

        return jdbcTemplate.query(sql, (rs, rn) -> mapDog(rs), userId, start, end);
    }

    private WalkCore mapWalkCore(ResultSet rs) throws SQLException {
        return new WalkCore(
                rs.getLong("distanceMeters"),
                rs.getLong("durationSeconds"),
                rs.getInt("humanWalkCaloriesKcal")
        );
    }

    private DogCalorie mapDog(ResultSet rs) throws SQLException {
        return new DogCalorie(
                rs.getString("name"),
                rs.getInt("dogWalkCaloriesKcal")
        );
    }
}