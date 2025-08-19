package org.chapchap.be.domain.route.dto;

import java.util.List;

// ORS 응답 파싱용
public record ORSDirectionsResponse(
        List<Route> routes
) {
    public record Route(
            Summary summary,
            String geometry // encoded polyline (polyline5)
    ) {}

    public record Summary(
            double distance,   // meters
            double duration    // seconds
    ) {}
}
