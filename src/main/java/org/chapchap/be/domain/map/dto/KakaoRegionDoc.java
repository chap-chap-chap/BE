package org.chapchap.be.domain.map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// 좌표 → 행정구역
public record KakaoRegionDoc(
        @JsonProperty("region_type") String regionType, // H 또는 B
        @JsonProperty("address_name") String addressName,
        @JsonProperty("region_1depth_name") String region1,
        @JsonProperty("region_2depth_name") String region2,
        @JsonProperty("region_3depth_name") String region3,
        @JsonProperty("region_4depth_name") String region4,
        String code,
        Double x,
        Double y
) {}