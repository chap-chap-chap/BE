package org.chapchap.be.domain.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.chapchap.be.domain.map.dto.*;

@Service
@RequiredArgsConstructor
public class KakaoLocalService {

    private final RestClient kakaoRestClient;

    public KakaoResponse<KakaoAddressDoc> searchAddress(String query, Integer page, Integer size, String analyzeType) {
        return kakaoRestClient.get()
                .uri(uri -> uri.path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("analyze_type", analyzeType)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) ->
                        new IllegalStateException("Kakao 4xx: " + res.getStatusCode()))
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) ->
                        new IllegalStateException("Kakao 5xx: " + res.getStatusCode()))
                .body(new ParameterizedTypeReference<>() {});
    }

    public KakaoResponse<KakaoRegionDoc> coord2region(double x, double y, String inputCoord, String outputCoord) {
        return kakaoRestClient.get()
                .uri(uri -> uri.path("/v2/local/geo/coord2regioncode.json")
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("input_coord", inputCoord)
                        .queryParam("output_coord", outputCoord)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public KakaoResponse<KakaoCoordAddressDoc> coord2address(double x, double y, String inputCoord) {
        return kakaoRestClient.get()
                .uri(uri -> uri.path("/v2/local/geo/coord2address.json")
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("input_coord", inputCoord)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public KakaoResponse<KakaoTransCoordDoc> transcoord(double x, double y, String inputCoord, String outputCoord) {
        return kakaoRestClient.get()
                .uri(uri -> uri.path("/v2/local/geo/transcoord.json")
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("input_coord", inputCoord)
                        .queryParam("output_coord", outputCoord)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public KakaoResponse<KakaoPlaceDoc> searchKeyword(String query, Double x, Double y, Integer radius,
                                                      Integer page, Integer size, String sort) {
        return kakaoRestClient.get()
                .uri(uri -> {
                    var b = uri.path("/v2/local/search/keyword.json")
                            .queryParam("query", query)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .queryParam("sort", sort);
                    if (x != null && y != null) {
                        b.queryParam("x", x).queryParam("y", y);
                    }
                    if (radius != null) {
                        b.queryParam("radius", radius);
                    }
                    return b.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public KakaoResponse<KakaoPlaceDoc> searchCategory(String categoryGroupCode,
                                                       Double x, Double y, Integer radius,
                                                       String rect,
                                                       Integer page, Integer size, String sort) {
        return kakaoRestClient.get()
                .uri(uri -> {
                    var b = uri.path("/v2/local/search/category.json")
                            .queryParam("category_group_code", categoryGroupCode)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .queryParam("sort", sort);
                    // (x,y,radius) 또는 rect 필수
                    if (rect != null && !rect.isBlank()) {
                        b.queryParam("rect", rect);
                    } else {
                        b.queryParam("x", x).queryParam("y", y).queryParam("radius", radius);
                    }
                    return b.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}