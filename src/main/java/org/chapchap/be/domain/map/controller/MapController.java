package org.chapchap.be.domain.map.controller;

import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.chapchap.be.domain.map.dto.*;
import org.chapchap.be.domain.map.service.KakaoLocalService;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Validated
public class MapController {

    private final KakaoLocalService kakao;

    // 주소 → 좌표
    @GetMapping("/address/search")
    public KakaoResponse<KakaoAddressDoc> address(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") @Min(1) @Max(45) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(30) Integer size,
            @RequestParam(defaultValue = "similar") String analyzeType
    ) {
        return kakao.searchAddress(query, page, size, analyzeType);
    }

    // 좌표 → 행정구역
    @GetMapping("/region/by-coord")
    public KakaoResponse<KakaoRegionDoc> region(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam(defaultValue = "WGS84") String inputCoord,
            @RequestParam(defaultValue = "WGS84") String outputCoord
    ) {
        return kakao.coord2region(x, y, inputCoord, outputCoord);
    }

    // 좌표 → 주소
    @GetMapping("/address/by-coord")
    public KakaoResponse<KakaoCoordAddressDoc> coord2addr(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam(defaultValue = "WGS84") String inputCoord
    ) {
        return kakao.coord2address(x, y, inputCoord);
    }

    // 좌표계 변환
    @GetMapping("/coord/transform")
    public KakaoResponse<KakaoTransCoordDoc> trans(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam(defaultValue = "WGS84") String inputCoord,
            @RequestParam(defaultValue = "WGS84") String outputCoord
    ) {
        return kakao.transcoord(x, y, inputCoord, outputCoord);
    }

    // 키워드 검색
    @GetMapping("/places/search")
    public KakaoResponse<KakaoPlaceDoc> keyword(
            @RequestParam String query,
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(required = false) Integer radius, // 0~20000
            @RequestParam(defaultValue = "1") @Min(1) @Max(45) Integer page,
            @RequestParam(defaultValue = "15") @Min(1) @Max(15) Integer size,
            @RequestParam(defaultValue = "accuracy") String sort // accuracy | distance
    ) {
        return kakao.searchKeyword(query, x, y, radius, page, size, sort);
    }

    // 카테고리 검색
    @GetMapping("/places/by-category")
    public KakaoResponse<KakaoPlaceDoc> byCategory(
            @RequestParam String categoryGroupCode, // 예: HP8(병원), PM9(약국) 등
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String rect,
            @RequestParam(defaultValue = "1") @Min(1) @Max(45) Integer page,
            @RequestParam(defaultValue = "15") @Min(1) @Max(15) Integer size,
            @RequestParam(defaultValue = "accuracy") String sort
    ) {
        return kakao.searchCategory(categoryGroupCode, x, y, radius, rect, page, size, sort);
    }
}