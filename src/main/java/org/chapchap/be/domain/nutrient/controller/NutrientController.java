package org.chapchap.be.domain.nutrient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.nutrient.dto.NutrientRequest;
import org.chapchap.be.global.config.PythonApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Nutrient")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NutrientController {

    private final PythonApiClient pythonApiClient = new PythonApiClient("http://fastapi:8000");

    @Operation(summary = "사료 영양소 체크")
    @PostMapping("/check_nutrient")
    public ResponseEntity<String> checkNutrient(@RequestBody NutrientRequest request) {
        // 사료의 영양소 적정 여부를 FastAPI 서버로부터 응답
        String result = pythonApiClient.checkNutrient(request.getNutrient(), request.getValue());
        return ResponseEntity.ok(result);
    }
}