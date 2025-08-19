package org.chapchap.be.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 주변 동물병원 검색 요청 DTO")
public record VetHospitalRequest(
        @Schema(description = "현재 위치 위도", example = "37.5665") double latitude,
        @Schema(description = "현재 위치 경도", example = "126.9780") double longitude,
        @Schema(description = "영업중만 필터(선택)", example = "false") Boolean openNow
) {}
