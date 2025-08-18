package org.chapchap.be.domain.route.service;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.route.dto.ORSDirectionsResponse;
import org.chapchap.be.domain.route.dto.RouteRequest;
import org.chapchap.be.domain.route.dto.RouteResponse;
import org.chapchap.be.global.exception.ExternalApiException;
import org.chapchap.be.global.exception.NoRouteFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final WebClient orsRoutesClient;

    public RouteResponse computeWalk(RouteRequest req) {
        // ORS Directions v2: /v2/directions/foot-walking
        Map<String, Object> body = Map.of(
                "coordinates", List.of(
                        List.of(req.origin().longitude(), req.origin().latitude()),       // [lon, lat]
                        List.of(req.destination().longitude(), req.destination().latitude())
                ),
                "instructions", false,                 // turn-by-turn 불필요
                "geometry", true,
                "elevation", false,
                "preference", "recommended",
                "units", "m"
        );

        try {
            ORSDirectionsResponse res = orsRoutesClient.post()
                    .uri("/v2/directions/foot-walking")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            r -> r.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new ExternalApiException(r.statusCode().value(),
                                            "ORS 4xx: " + msg)))
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            r -> r.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new ExternalApiException(r.statusCode().value(),
                                            "ORS 5xx: " + msg)))
                    )
                    .bodyToMono(ORSDirectionsResponse.class)
                    .timeout(Duration.ofSeconds(6))
                    .block();

            if (res == null || res.routes() == null || res.routes().isEmpty()) {
                throw new NoRouteFoundException("보행 경로를 찾지 못했습니다. 출발/도착 좌표를 확인하세요.");
            }

            var r = res.routes().get(0);
            if (r.summary() == null || r.geometry() == null) {
                throw new NoRouteFoundException("경로 응답이 불완전합니다.");
            }

            int distance = (int) Math.round(r.summary().distance()); // meters
            long seconds = Math.round(r.summary().duration());       // seconds
            String encoded = r.geometry();                           // encoded polyline

            return new RouteResponse(distance, seconds, encoded);

        } catch (WebClientResponseException e) {
            // retrieve().onStatus에서 잡지 못한 케이스(네트워크 경계 등)
            throw new ExternalApiException(e.getStatusCode().value(),
                    "ORS 호출 실패: " + e.getResponseBodyAsString());
        } catch (NoRouteFoundException e) {
            throw e;
        } catch (Exception e) {
            // 타임아웃 등 기타
            throw new ExternalApiException(502, "ORS 호출 실패: " + e.getMessage());
        }
    }
}
