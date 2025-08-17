package org.chapchap.be.domain.map.dto;

// 좌표계 변환
public record KakaoTransCoordDoc(
        Double x, // 변환된 경도
        Double y  // 변환된 위도
) {}