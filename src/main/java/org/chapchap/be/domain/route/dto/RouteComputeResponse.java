package org.chapchap.be.domain.route.dto;

public record RouteComputeResponse(
        long distanceMeters,
        long durationSeconds,
        String encodedPolyline
) {}