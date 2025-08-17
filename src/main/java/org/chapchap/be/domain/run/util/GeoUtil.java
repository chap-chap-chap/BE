package org.chapchap.be.domain.run.util;

public class GeoUtil {
    private static final double R = 6371000.0; // meters

    // 두 점 거리(m)
    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    // 시작점(lat,lon), 방위각(bearingDeg), 거리(m) → 도착점(lat,lon)
    public static double[] destinationPoint(double lat, double lon, double bearingDeg, double distanceMeters) {
        double δ = distanceMeters / R;
        double θ = Math.toRadians(bearingDeg);
        double φ1 = Math.toRadians(lat);
        double λ1 = Math.toRadians(lon);

        double φ2 = Math.asin(Math.sin(φ1)*Math.cos(δ) + Math.cos(φ1)*Math.sin(δ)*Math.cos(θ));
        double λ2 = λ1 + Math.atan2(Math.sin(θ)*Math.sin(δ)*Math.cos(φ1),
                Math.cos(δ)-Math.sin(φ1)*Math.sin(φ2));
        return new double[]{ Math.toDegrees(φ2), Math.toDegrees(λ2) };
    }
}