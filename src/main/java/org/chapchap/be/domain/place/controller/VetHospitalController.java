package org.chapchap.be.domain.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.place.dto.VetHospitalRequest;
import org.chapchap.be.domain.place.dto.VetHospitalResponse;
import org.chapchap.be.domain.place.service.VetHospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vet Hospital")
@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class VetHospitalController {

    private final VetHospitalService vetHospitalService;

    @Operation(summary = "내 주변 동물병원 검색 (기본: 반경 2km, 최대 10개, 상세정보 포함)")
    @PostMapping("/hospital")
    public ResponseEntity<VetHospitalResponse> search(@RequestBody VetHospitalRequest req) {
        return ResponseEntity.ok(vetHospitalService.search(req));
    }
}
