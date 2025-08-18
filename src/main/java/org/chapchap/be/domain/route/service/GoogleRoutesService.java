package org.chapchap.be.domain.route.service;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.route.dto.RouteComputeRequest;
import org.chapchap.be.domain.route.dto.RouteComputeResponse;
import org.chapchap.be.domain.route.dto.GoogleRoutesResponse;
import org.chapchap.be.global.exception.GoogleApiException;
import org.chapchap.be.global.exception.NoRouteFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleRoutesService {

    private final WebClient googleRoutesClient;

    public RouteComputeResponse computeRoute(RouteComputeRequest req) {
        String mode = (req.travelMode() == null || req.travelMode().isBlank())
                ? "WALK" : req.travelMode().toUpperCase();

        // Google REST 바디 구성
        Map<String, Object> body = Map.of(
                "origin", Map.of(
                        "location", Map.of(
                                "latLng", Map.of(
                                        "latitude", req.origin().latitude(),
                                        "longitude", req.origin().longitude()
                                )
                        )
                ),
                "destination", Map.of(
                        "location", Map.of(
                                "latLng", Map.of(
                                        "latitude", req.destination().latitude(),
                                        "longitude", req.destination().longitude()
                                )
                        )
                ),
                "travelMode", mode,                              // "WALK" or "DRIVE"
                // 드라이브일 때만 교통 고려
                "routingPreference", mode.equals("DRIVE") ? "TRAFFIC_AWARE" : "ROUTING_PREFERENCE_UNSPECIFIED",
                "computeAlternativeRoutes", false,
                "routeModifiers", Map.of(
                        "avoidTolls", false,
                        "avoidHighways", false,
                        "avoidFerries", false
                ),
                "languageCode", "ko",
                "units", "METRIC"
        );

        try {
            GoogleRoutesResponse res = googleRoutesClient.post()
                    .uri("/directions/v2:computeRoutes")
                    .header("X-Goog-FieldMask",
                            "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, r -> r.bodyToMono(String.class)
                            .map(msg -> new GoogleApiException(r.statusCode().value(), "Google Routes 4xx: " + msg)))
                    .onStatus(HttpStatusCode::is5xxServerError, r -> r.bodyToMono(String.class)
                            .map(msg -> new GoogleApiException(r.statusCode().value(), "Google Routes 5xx: " + msg)))
                    .bodyToMono(GoogleRoutesResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (res == null || res.routes() == null || res.routes().isEmpty()) {
                // Google이 빈 JSON 응답을 주는 케이스 방어
                throw new NoRouteFoundException("경로를 찾지 못했습니다. 좌표/모드를 확인하세요.");
            }

            var r = res.routes().get(0);
            if (r.distanceMeters() == null || r.duration() == null || r.polyline() == null) {
                throw new NoRouteFoundException("경로 응답이 불완전합니다.");
            }

            long seconds = parseDurationSeconds(r.duration()); // "165s" -> 165

            return new RouteComputeResponse(
                    r.distanceMeters(),
                    seconds,
                    r.polyline().encodedPolyline()
            );
        } catch (WebClientResponseException e) {
            throw new GoogleApiException(e.getStatusCode().value(),
                    "Google Routes error: " + e.getResponseBodyAsString());
        } catch (NoRouteFoundException e) {
            throw e;
        } catch (Exception e) {
            // 타임아웃 등 기타
            throw new GoogleApiException(502, "Google Routes 호출 실패: " + e.getMessage());
        }
    }

    private static long parseDurationSeconds(String s) {
        // 포맷 예: "165s", "1.234s"
        if (s == null || !s.endsWith("s")) return 0L;
        String num = s.substring(0, s.length() - 1);
        double d = Double.parseDouble(num);
        return (long) Math.floor(d);
    }
}