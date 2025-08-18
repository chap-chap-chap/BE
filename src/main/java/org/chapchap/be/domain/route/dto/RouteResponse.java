package org.chapchap.be.domain.route.dto;

public record RouteResponse(
        int distanceMeters,
        long durationSeconds,
        String encodedPolyline
) {}
