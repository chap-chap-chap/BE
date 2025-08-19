package org.chapchap.be.domain.place.service;

import org.chapchap.be.global.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.List;

@Component
public class GooglePlacesService {

    private final WebClient googlePlacesClient;

    public GooglePlacesService(@Qualifier("googlePlacesClient") WebClient googlePlacesClient) {
        this.googlePlacesClient = googlePlacesClient;
    }

    @Value("${app.google.places.api-key}")
    private String apiKey;

    public NearbyResp nearbySearch(double lat, double lng, int radiusMeters, String type, String keyword,
                                   boolean openNow, String language, String region) {
        String url = buildNearbyUrl(lat, lng, radiusMeters, type, keyword, openNow, language, region);
        try {
            return googlePlacesClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(s -> s.is4xxClientError(),
                            r -> r.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new ExternalApiException(r.statusCode().value(),
                                            "Google Places 4xx: " + msg))))
                    .onStatus(s -> s.is5xxServerError(),
                            r -> r.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new ExternalApiException(r.statusCode().value(),
                                            "Google Places 5xx: " + msg))))
                    .bodyToMono(NearbyResp.class)
                    .timeout(Duration.ofSeconds(6))
                    .block();
        } catch (WebClientResponseException e) {
            throw new ExternalApiException(e.getStatusCode().value(), "Google Places 호출 실패: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ExternalApiException(502, "Google Places 호출 실패: " + e.getMessage());
        }
    }

    public DetailsResp placeDetails(String placeId, String fields, String language, String region) {
        String url = buildDetailsUrl(placeId, fields, language, region);
        try {
            return googlePlacesClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(DetailsResp.class)
                    .timeout(Duration.ofSeconds(4))
                    .block();
        } catch (Exception e) {
            // 상세 실패는 치명적이지 않으므로 상위에서 무시 가능
            return null;
        }
    }

    /* -------- URL: StringBuilder 경고 제거 & 안전한 인코딩 -------- */

    private String buildNearbyUrl(double lat, double lng, int radius, String type, String keyword, boolean openNow,
                                  String language, String region) {
        UriComponentsBuilder b = UriComponentsBuilder
                .fromPath("/maps/api/place/nearbysearch/json")
                .queryParam("location", lat + "," + lng)
                .queryParam("radius", radius)
                .queryParam("type", type)
                .queryParam("language", language)
                .queryParam("region", region)
                .queryParam("key", apiKey);

        if (keyword != null && !keyword.isBlank()) {
            b.queryParam("keyword", keyword);
        }
        if (openNow) {
            b.queryParam("opennow", "true");
        }
        return b.toUriString();
    }

    private String buildDetailsUrl(String placeId, String fields, String language, String region) {
        return UriComponentsBuilder
                .fromPath("/maps/api/place/details/json")
                .queryParam("place_id", placeId)
                .queryParam("fields", fields)
                .queryParam("language", language)
                .queryParam("region", region)
                .queryParam("key", apiKey)
                .toUriString();
    }

    /* -------- Google 응답 맵핑 -------- */

    public record NearbyResp(List<Result> results, String next_page_token) {
        public record Result(
                String place_id,
                String name,
                String vicinity,
                Geometry geometry,
                OpeningHours opening_hours,
                Double rating,
                Integer user_ratings_total
        ) {}
        public record Geometry(Location location) {}
        public record Location(double lat, double lng) {}
        public record OpeningHours(Boolean open_now) {}
    }

    public record DetailsResp(DetailsResult result) {
        public record DetailsResult(
                String formatted_phone_number,
                String website,
                OpeningHours opening_hours,
                String formatted_address
        ) {}
        public record OpeningHours(List<String> weekday_text) {}
    }
}