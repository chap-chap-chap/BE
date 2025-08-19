package org.chapchap.be.domain.route.service;

import org.chapchap.be.domain.route.dto.ORSDirectionsResponse;
import org.chapchap.be.domain.route.dto.RouteRequest;
import org.chapchap.be.domain.route.dto.RouteResponse;
import org.chapchap.be.domain.route.entity.WalkDogCalorie;
import org.chapchap.be.domain.route.entity.WalkRoute;
import org.chapchap.be.domain.route.repository.WalkRouteRepository;
import org.chapchap.be.domain.route.util.CalorieUtil;
import org.chapchap.be.domain.dog.entity.Dog;
import org.chapchap.be.domain.dog.repository.DogRepository;
import org.chapchap.be.domain.user.repository.UserProfileRepository;
import org.chapchap.be.domain.user.repository.UserRepository;
import org.chapchap.be.global.exception.ExternalApiException;
import org.chapchap.be.global.exception.NoRouteFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class RouteService {

    private final WebClient orsRoutesClient;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final DogRepository dogRepository;
    private final WalkRouteRepository walkRouteRepository;

    public RouteService(@Qualifier("orsRoutesClient") WebClient orsRoutesClient,
                        UserRepository userRepository,
                        UserProfileRepository userProfileRepository,
                        DogRepository dogRepository,
                        WalkRouteRepository walkRouteRepository) {
        this.orsRoutesClient = orsRoutesClient;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.dogRepository = dogRepository;
        this.walkRouteRepository = walkRouteRepository;
    }

    @Transactional
    public RouteResponse computeWalk(RouteRequest req) {
        // ORS Directions v2: /v2/directions/foot-walking
        // ORS 호출
        Map<String, Object> body = Map.of(
                "coordinates", List.of(
                        List.of(req.origin().longitude(), req.origin().latitude()),
                        List.of(req.destination().longitude(), req.destination().latitude())
                ),
                "instructions", false,
                "geometry", true,
                "elevation", false,
                "preference", "recommended",
                "units", "m"
        );

        ORSDirectionsResponse res;
        try {
            res = orsRoutesClient.post()
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

        if (res == null || res.routes() == null || res.routes().isEmpty()) {
            throw new NoRouteFoundException("보행 경로를 찾지 못했습니다. 출발/도착 좌표를 확인하세요.");
        }
        var r = res.routes().get(0);
        if (r.summary() == null || r.geometry() == null) {
            throw new NoRouteFoundException("경로 응답이 불완전합니다.");
        }

        int distanceMeters = (int) Math.round(r.summary().distance());  // meters
        long durationSeconds = Math.round(r.summary().duration());      // seconds
        String encodedPolyline = r.geometry();                          // encoded polyline

        // 사용자/프로필
        var user = userRepository.findByEmail(currentAuth().getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        var profile = userProfileRepository.findByUserId(user.getId()).orElse(null);

        int humanDaily = CalorieUtil.humanDailyKcal(profile);
        int humanWalk  = CalorieUtil.humanWalkKcal(
                distanceMeters, durationSeconds,
                profile != null ? profile.getHumanWeightKg() : null
        );

        // 강아지 목록 - 강아지 이름 기준 조회, 비어 있으면 등록된 강아지 전체(archived=false 가정)
        List<Dog> dogsToCalc;
        if (req.dogNames() != null && !req.dogNames().isEmpty()) {
            dogsToCalc = req.dogNames().stream()
                    .map(name -> dogRepository.findByNameAndOwnerId(name, user.getId())
                            .orElseThrow(() -> new IllegalArgumentException("강아지를 찾을 수 없습니다: name=" + name)))
                    .toList();
        } else {
            dogsToCalc = user.getDogs().stream()
                    .filter(d -> !Boolean.TRUE.equals(d.getArchived()))
                    .toList();
        }

        // 강아지별 칼로리
        List<RouteResponse.DogCalorie> dogCalList = dogsToCalc.stream()
                .map(d -> {
                    int daily = CalorieUtil.dogDailyKcal(d.getWeightKg(), d.getAgeMonths());
                    int walk  = CalorieUtil.dogWalkKcal(distanceMeters, d.getWeightKg());
                    return new RouteResponse.DogCalorie(
                            d.getId(), d.getName(), d.getAgeMonths(), d.getWeightKg(), daily, walk
                    );
                })
                .toList();

        // DB 저장 (헤더 + 디테일)
        WalkRoute route = WalkRoute.builder()
                .owner(user)
                .originLat(req.origin().latitude())
                .originLng(req.origin().longitude())
                .destLat(req.destination().latitude())
                .destLng(req.destination().longitude())
                .distanceMeters(distanceMeters)
                .durationSeconds(durationSeconds)
                .encodedPolyline(encodedPolyline)
                .humanDailyCaloriesKcal(humanDaily)
                .humanWalkCaloriesKcal(humanWalk)
                .build();

        dogCalList.forEach(dc -> {
            route.addDogCalorie(
                    WalkDogCalorie.builder()
                            .dogId(dc.dogId())           // null 허용 가능
                            .dogName(dc.name())
                            .ageMonths(dc.ageMonths())
                            .weightKg(dc.weightKg())
                            .dailyCaloriesKcal(dc.dogDailyCaloriesKcal())
                            .walkCaloriesKcal(dc.dogWalkCaloriesKcal())
                            .build()
            );
        });

        walkRouteRepository.save(route); // cascade로 디테일까지 저장

        // 6) 응답은 기존과 동일
        return new RouteResponse(
                distanceMeters,
                durationSeconds,
                encodedPolyline,
                humanDaily,
                humanWalk,
                dogCalList
        );
    }

    private Authentication currentAuth() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }
        return auth;
    }
}
