package org.chapchap.be.domain.nutrient.controller;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.nutrient.dto.NutrientRequest;
import org.chapchap.be.global.config.PythonApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NutrientController {

    private final PythonApiClient pythonApiClient = new PythonApiClient("http://fastapi:8000");

    @PostMapping("/check_nutrient")
    public ResponseEntity<String> checkNutrient(@RequestBody NutrientRequest request) {
        String result = pythonApiClient.checkNutrient(request.getNutrient(), request.getValue());
        return ResponseEntity.ok(result);
    }
}