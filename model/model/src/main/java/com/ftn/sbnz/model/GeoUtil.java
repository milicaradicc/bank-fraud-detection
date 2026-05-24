package com.ftn.sbnz.model;

public class GeoUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double MAX_TRAVEL_SPEED_KMH = 950.0;

    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public static boolean isImpossibleTravel(double lat1, double lon1,
                                             double lat2, double lon2,
                                             long elapsedMs) {
        if (elapsedMs <= 0) return false;
        double distance = distanceKm(lat1, lon1, lat2, lon2);
        double hours = elapsedMs / (1000.0 * 60.0 * 60.0);
        double requiredSpeed = distance / hours;
        return requiredSpeed > MAX_TRAVEL_SPEED_KMH;
    }
}