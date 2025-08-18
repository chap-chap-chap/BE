package org.chapchap.be.domain.run.service;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.map.dto.KakaoPlaceDoc;
import org.chapchap.be.domain.map.dto.KakaoResponse;
import org.chapchap.be.domain.map.service.KakaoLocalService;
import org.chapchap.be.domain.run.dto.RunPlanRequest;
import org.chapchap.be.domain.run.dto.RunPlanResponse;
import org.chapchap.be.domain.run.dto.RunPlanResponse.Poi;
import org.chapchap.be.domain.run.dto.RunPlanResponse.Waypoint;
import org.chapchap.be.domain.run.util.GeoUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RunPlanService {

    private final KakaoLocalService kakao;

    public RunPlanResponse plan(RunPlanRequest req) {
        double pace = Optional.ofNullable(req.paceMinPerKm()).orElse(6.0);
        boolean preferParks = Optional.ofNullable(req.preferParks()).orElse(true);

        double targetKm = Math.max(0.8, req.minutes() / pace); // 최소 0.8km
        double targetMeters = targetKm * 1000.0;

        // 1) 목표 거리와 가장 잘 맞는 공원/트랙 선택(여러 후보 중 스코어링)
        Poi poi = null;
        if (preferParks) {
            poi = findBestPoiForTarget(req.lat(), req.lng(), targetMeters);
        }

        // 2) 코스 생성
        List<Waypoint> waypoints;
        if (poi != null) {
            waypoints = buildPoiLoopCourse(req.lat(), req.lng(), poi, targetMeters);
        } else {
            waypoints = buildOutAndBack(req.lat(), req.lng(), targetMeters);
        }

        // 3) 총거리 근사
        double total = 0;
        for (int i = 1; i < waypoints.size(); i++) {
            Waypoint a = waypoints.get(i - 1);
            Waypoint b = waypoints.get(i);
            total += GeoUtil.distanceMeters(a.lat(), a.lng(), b.lat(), b.lng());
        }

        return new RunPlanResponse(
                targetKm,
                req.minutes(),
                poi,
                waypoints,
                Math.round(total / 100.0) / 10.0, // 소수1자리 반올림
                "draw in order"
        );
    }

    /**
     * 목표 총거리(targetMeters)를 기준으로,
     * - idealOneWay ≈ 왕복+루프를 고려한 집→공원 ‘한쪽’ 이상적인 거리
     * - searchRadius: target에 비례(최소/최대 clamp)
     * 반경 내 후보들을 모아 idealOneWay와의 차이가 최소인 공원을 선택.
     */
    private Poi findBestPoiForTarget(double lat, double lng, double targetMeters) {
        double idealOneWay = computeIdealOneWayMeters(targetMeters);   // 예: target의 35% (400m ~ 3500m)
        int searchRadius = (int) computeSearchRadiusMeters(targetMeters); // 예: target의 60% (800m ~ 6000m)

        List<KakaoPlaceDoc> candidates = new ArrayList<>();
        // 키워드 다양화(가중치 없이 합산): 공원/운동장/산책로
        for (String q : List.of("공원", "운동장", "산책로")) {
            // Kakao API는 페이지당 size<=15, page<=45
            for (int page = 1; page <= 3; page++) { // 최대 45개 정도 확보(15*3)
                KakaoResponse<KakaoPlaceDoc> res =
                        kakao.searchKeyword(q, lng, lat, searchRadius, page, 15, "distance");
                if (res == null || res.documents() == null || res.documents().isEmpty()) break;
                candidates.addAll(res.documents());
                if (Boolean.TRUE.equals(res.meta().isEnd())) break;
            }
        }

        if (candidates.isEmpty()) return null;

        // 스코어링: |거리-idealOneWay|가 작을수록 우선
        // 동점이면 이름에 "공원/운동장/산책로" 포함 우대, 그 다음 실제로 짧은 순
        KakaoPlaceDoc bestDoc = candidates.stream()
                .distinct()
                .min((a, b) -> {
                    double da = GeoUtil.distanceMeters(lat, lng, Double.parseDouble(a.y()), Double.parseDouble(a.x()));
                    double db = GeoUtil.distanceMeters(lat, lng, Double.parseDouble(b.y()), Double.parseDouble(b.x()));
                    double sa = Math.abs(da - idealOneWay);
                    double sb = Math.abs(db - idealOneWay);

                    int cmp = Double.compare(sa, sb);
                    if (cmp != 0) return cmp;

                    // 이름 가산점: "공원/운동장/산책로" 포함 우선
                    boolean aParkName = a.placeName() != null && (a.placeName().contains("공원") || a.placeName().contains("운동장") || a.placeName().contains("산책로"));
                    boolean bParkName = b.placeName() != null && (b.placeName().contains("공원") || b.placeName().contains("운동장") || b.placeName().contains("산책로"));
                    if (aParkName != bParkName) return aParkName ? -1 : 1;

                    // 마지막으로 실제距 짧은 순
                    return Double.compare(da, db);
                })
                .orElse(null);

        if (bestDoc == null) return null;

        double dMeters = GeoUtil.distanceMeters(lat, lng,
                Double.parseDouble(bestDoc.y()), Double.parseDouble(bestDoc.x()));

        return new Poi(
                bestDoc.placeName(),
                bestDoc.id(),
                Double.parseDouble(bestDoc.x()),
                Double.parseDouble(bestDoc.y()),
                (int) Math.round(dMeters)
        );
    }

    // 예: target의 약 35%를 집→공원 한쪽 거리로 사용(루프와 복귀 고려)
    private double computeIdealOneWayMeters(double targetMeters) {
        double ideal = targetMeters * 0.35; // 경험적 비율
        // 너무 가깝거나 먼 공원은 제외(최소/최대 제한)
        return clamp(ideal, 400, 3500); // 0.4km ~ 3.5km
    }

    // 검색 반경: target의 60% 정도(최소/최대 제한)
    private double computeSearchRadiusMeters(double targetMeters) {
        double r = targetMeters * 0.6;
        return clamp(r, 800, 6000); // 0.8km ~ 6km
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private List<Waypoint> buildPoiLoopCourse(double lat, double lng, Poi poi, double targetMeters) {
        List<Waypoint> pts = new ArrayList<>();
        pts.add(new Waypoint(lat, lng, "start"));

        double poiLat = Double.parseDouble("%.8f".formatted(poi.y()));
        double poiLng = Double.parseDouble("%.8f".formatted(poi.x()));

        // 사용자 → POI
        pts.add(new Waypoint(midPoint(lat, lng, poiLat, poiLng)[0],
                midPoint(lat, lng, poiLat, poiLng)[1], "to-park"));
        pts.add(new Waypoint(poiLat, poiLng, "park-entry"));

        // 현재까지 거리
        double base = cumulativeDistance(pts);

        // 남은 거리: 왕복 고려하여 반원 루프 길이 추정
        double remaining = Math.max(0, targetMeters - base * 2);
        double loopPerimeter = Math.min(Math.max(remaining, 300), 1400);
        double radius = loopPerimeter / Math.PI; // 반원 둘레 = πr

        double bearing0 = 60;
        int segments = 4;
        for (int i = 1; i <= segments; i++) {
            double bearing = bearing0 + 180.0 * (i / (double) segments);
            double[] p = GeoUtil.destinationPoint(poiLat, poiLng, bearing, radius);
            pts.add(new Waypoint(p[0], p[1], "park-loop"));
        }
        // 루프 종료 → 공원 진입점 복귀 → 출발지 복귀
        pts.add(new Waypoint(poiLat, poiLng, "park-entry"));
        pts.add(new Waypoint(midPoint(poiLat, poiLng, lat, lng)[0],
                midPoint(poiLat, poiLng, lat, lng)[1], "back"));
        pts.add(new Waypoint(lat, lng, "finish"));
        return pts;
    }

    private List<Waypoint> buildOutAndBack(double lat, double lng, double targetMeters) {
        List<Waypoint> pts = new ArrayList<>();
        pts.add(new Waypoint(lat, lng, "start"));
        double half = targetMeters / 2.0;
        double[] mid = GeoUtil.destinationPoint(lat, lng, 40.0, half);
        pts.add(new Waypoint(mid[0], mid[1], "turn-around"));
        pts.add(new Waypoint(lat, lng, "finish"));
        return pts;
    }

    private static double[] midPoint(double lat1, double lon1, double lat2, double lon2) {
        return new double[]{(lat1 + lat2) / 2.0, (lon1 + lon2) / 2.0};
    }

    private static double cumulativeDistance(List<Waypoint> pts) {
        double sum = 0;
        for (int i = 1; i < pts.size(); i++) {
            var a = pts.get(i - 1);
            var b = pts.get(i);
            sum += GeoUtil.distanceMeters(a.lat(), a.lng(), b.lat(), b.lng());
        }
        return sum;
    }
}