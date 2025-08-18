package org.chapchap.be.domain.route.dto;

import java.util.List;

public record ORSDirectionsResponse(
        List<Route> routes
) {
    public record Route(
            Summary summary,
            String geometry
    ) {}

    public record Summary(
            double distance,   // meters
            double duration    // seconds
    ) {}
}
