package org.chapchap.be.domain.map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// 좌표 → 주소
public record KakaoCoordAddressDoc(
        KakaoAddressDoc.Address address,
        @JsonProperty("road_address") KakaoAddressDoc.RoadAddress roadAddress
) {}