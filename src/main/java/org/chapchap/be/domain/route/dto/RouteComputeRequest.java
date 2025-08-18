package org.chapchap.be.domain.route.dto;

import jakarta.validation.constraints.NotNull;

public record RouteComputeRequest(
        @NotNull Point origin,
        @NotNull Point destination,
        // 선택: "WALK" | "DRIVE"; 없으면 WALK 기본
        String travelMode
) {
    public record Point(
            @NotNull Double latitude,
            @NotNull Double longitude
    ) {}
}