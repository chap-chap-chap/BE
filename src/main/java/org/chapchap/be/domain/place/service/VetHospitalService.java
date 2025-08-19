package org.chapchap.be.domain.place.service;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.place.dto.VetHospitalRequest;
import org.chapchap.be.domain.place.dto.VetHospitalResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VetHospitalService {

    private final GooglePlacesService google;

    // 기본값(요청 바디에 radius/max/enrich 없음)
    private static final int DEFAULT_RADIUS_METERS = 2000; // 2km
    private static final int DEFAULT_MAX_RESULTS   = 10;
    private static final boolean DEFAULT_ENRICH    = true; // 전화/영업시간/정식주소

    public VetHospitalResponse search(VetHospitalRequest req) {
        boolean openNow = req.openNow() != null && req.openNow();

        var nearby = google.nearbySearch(
                req.latitude(),
                req.longitude(),
                DEFAULT_RADIUS_METERS,
                "veterinary_care",
                null,
                openNow,
                "ko", "KR"
        );

        if (nearby == null || nearby.results() == null || nearby.results().isEmpty()) {
            return new VetHospitalResponse(0, List.of());
        }

        List<VetHospitalResponse.Information> infoList = new ArrayList<>();

        for (var r : nearby.results()) {
            if (infoList.size() >= DEFAULT_MAX_RESULTS) break;

            double lat = (r.geometry() != null && r.geometry().location() != null) ? r.geometry().location().lat() : 0;
            double lng = (r.geometry() != null && r.geometry().location() != null) ? r.geometry().location().lng() : 0;
            double distanceKm = round1(haversineKm(req.latitude(), req.longitude(), lat, lng));

            String address = (r.vicinity() != null) ? r.vicinity() : "";
            List<String> openingHours = null;
            String phone = null;

            if (DEFAULT_ENRICH && r.place_id() != null) {
                var details = google.placeDetails(
                        r.place_id(),
                        "formatted_phone_number,website,opening_hours,formatted_address",
                        "ko", "KR"
                );
                if (details != null && details.result() != null) {
                    if (details.result().formatted_address() != null) {
                        address = details.result().formatted_address();
                    }
                    phone = details.result().formatted_phone_number();
                    if (details.result().opening_hours() != null) {
                        openingHours = details.result().opening_hours().weekday_text();
                    }
                }
            }

            infoList.add(new VetHospitalResponse.Information(
                    r.place_id(),
                    r.name(),
                    address,
                    lat,
                    lng,
                    distanceKm,
                    r.rating(),
                    r.user_ratings_total(),
                    (r.opening_hours() != null) ? r.opening_hours().open_now() : null,
                    openingHours,
                    phone
            ));
        }

        return new VetHospitalResponse(infoList.size(), infoList);
    }

    /* ---------- 거리 계산(하버사인) & 반올림 ---------- */
    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
}
