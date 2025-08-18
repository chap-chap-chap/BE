package org.chapchap.be.domain.route.dto;

public record RouteRequest(
        Point origin,
        Point destination
) {
    public record Point(double latitude, double longitude) {}
}
