package org.chapchap.be.domain.route.dto;

import java.util.List;

public record GoogleRoutesResponse(
        List<Route> routes
) {
    public record Route(
            Integer distanceMeters,
            String duration,               // e.g. "165s"
            Polyline polyline
    ) {}

    public record Polyline(
            String encodedPolyline
    ) {}
}