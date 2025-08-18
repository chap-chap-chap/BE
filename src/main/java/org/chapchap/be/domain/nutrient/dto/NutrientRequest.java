package org.chapchap.be.domain.nutrient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "영양소 요청 DTO")
@Data
public class NutrientRequest {
    @Schema(description = "영양소 이름", example = "단백질")
    private String nutrient;

    @Schema(description = "영양소 값", example = "25.0")
    private double value;
}
