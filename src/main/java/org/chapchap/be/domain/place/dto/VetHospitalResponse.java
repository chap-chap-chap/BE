package org.chapchap.be.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "동물병원 검색 응답 DTO")
public record VetHospitalResponse(
        @Schema(description = "총 반환 건수", example = "10") int count,
        @Schema(description = "동물병원 정보 목록") List<Information> information
) {
    @Schema(description = "동물병원 정보")
    public record Information(
            @Schema(description = "Place ID", example = "ChIJy...") String placeId,
            @Schema(description = "이름", example = "행복한동물병원") String name,
            @Schema(description = "주소", example = "서울 강남구 선릉로 789") String address,
            @Schema(description = "위도", example = "37.5668") double lat,
            @Schema(description = "경도", example = "126.9785") double lng,
            @Schema(description = "내 위치로부터 거리(km)", example = "1.3") double distanceKm,
            @Schema(description = "평점(0~5)", example = "4.6") Double rating,
            @Schema(description = "리뷰 수", example = "132") Integer userRatingsTotal,
            @Schema(description = "현재 영업중 여부", example = "true") Boolean openNow,
            @Schema(description = "영업 시간 텍스트(월~일)", example = "[\"월 09:00–18:00\", \"화 09:00–18:00\", ...]") List<String> openingHours,
            @Schema(description = "전화번호", example = "02-1234-5678") String phone
    ) {}
}
