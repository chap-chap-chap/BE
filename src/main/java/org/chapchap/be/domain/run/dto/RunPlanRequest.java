package org.chapchap.be.domain.run.dto;

import jakarta.validation.constraints.*;

public record RunPlanRequest(
        @NotNull @DecimalMin("-90") @DecimalMax("90") Double lat,
        @NotNull @DecimalMin("-180") @DecimalMax("180") Double lng,
        @NotNull @Min(5) @Max(240) Integer minutes,
        @DecimalMin("3.0") @DecimalMax("10.0") Double paceMinPerKm, // default 6.0
        Boolean preferParks // default true
) {}