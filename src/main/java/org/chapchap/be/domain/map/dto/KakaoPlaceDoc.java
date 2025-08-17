package org.chapchap.be.domain.map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// 키워드/카테고리 장소
public record KakaoPlaceDoc(
        String id,
        @JsonProperty("place_name") String placeName,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("category_group_code") String categoryGroupCode,
        @JsonProperty("category_group_name") String categoryGroupName,
        String phone,
        @JsonProperty("address_name") String addressName,
        @JsonProperty("road_address_name") String roadAddressName,
        String x, // 경도
        String y, // 위도
        @JsonProperty("place_url") String placeUrl,
        String distance // 선택적(거리 정렬/좌표 제공 시)
) {}