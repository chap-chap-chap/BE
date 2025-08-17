package org.chapchap.be.domain.run.dto;

import java.util.List;

public record RunPlanResponse(
        double targetKm,
        int estimatedMinutes,
        Poi usedPoi,
        List<Waypoint> waypoints,
        double approxDistanceKm,
        String polylineHint
) {
    public record Waypoint(double lat, double lng, String note) {}
    public record Poi(String name, String id, double x, double y, int distanceToPoiMeters) {}
}