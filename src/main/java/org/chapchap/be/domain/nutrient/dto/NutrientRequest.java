package org.chapchap.be.domain.nutrient.dto;

import lombok.Data;

@Data
public class NutrientRequest {
    private String nutrient;
    private double value;
}