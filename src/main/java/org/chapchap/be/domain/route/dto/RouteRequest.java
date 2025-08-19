package org.chapchap.be.domain.route.dto;

import java.util.List;

public record RouteRequest(
        Point origin,
        Point destination,
        List<Long> dogIds // 선택된 강아지 id 리스트(없으면 null/빈배열)
) {
    public record Point(double latitude, double longitude) {}
}
