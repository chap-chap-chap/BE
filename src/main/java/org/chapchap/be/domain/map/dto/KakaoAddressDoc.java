package org.chapchap.be.domain.map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// 주소 → 좌표
public record KakaoAddressDoc(
        @JsonProperty("address_name") String addressName,
        @JsonProperty("address_type") String addressType, // REGION/ROAD/REGION_ADDR/ROAD_ADDR
        String x, // 경도
        String y, // 위도
        Address address,
        @JsonProperty("road_address") RoadAddress roadAddress
) {
    public record Address(
            @JsonProperty("address_name") String addressName,
            @JsonProperty("region_1depth_name") String region1depthName,
            @JsonProperty("region_2depth_name") String region2depthName,
            @JsonProperty("region_3depth_name") String region3depthName,
            @JsonProperty("region_3depth_h_name") String region3depthHName,
            @JsonProperty("h_code") String hCode,
            @JsonProperty("b_code") String bCode,
            @JsonProperty("mountain_yn") String mountainYn,
            @JsonProperty("main_address_no") String mainAddressNo,
            @JsonProperty("sub_address_no") String subAddressNo,
            String x,
            String y
    ) {}
    public record RoadAddress(
            @JsonProperty("address_name") String addressName,
            @JsonProperty("region_1depth_name") String region1depthName,
            @JsonProperty("region_2depth_name") String region2depthName,
            @JsonProperty("region_3depth_name") String region3depthName,
            @JsonProperty("road_name") String roadName,
            @JsonProperty("underground_yn") String undergroundYn,
            @JsonProperty("main_building_no") String mainBuildingNo,
            @JsonProperty("sub_building_no") String subBuildingNo,
            @JsonProperty("building_name") String buildingName,
            @JsonProperty("zone_no") String zoneNo,
            String x,
            String y
    ) {}
}