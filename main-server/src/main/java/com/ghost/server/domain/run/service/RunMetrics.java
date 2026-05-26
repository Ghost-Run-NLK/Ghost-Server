package com.ghost.server.domain.run.service;

import com.ghost.server.common.util.GeoUtils;
import com.ghost.server.domain.run.entity.TrackPoint;

import java.util.List;

public final class RunMetrics {

    private RunMetrics() {
    }

    public static int distanceMeters(List<TrackPoint> orderedPoints) {
        if (orderedPoints.size() < 2) {
            return 0;
        }
        double total = 0;
        for (int i = 1; i < orderedPoints.size(); i++) {
            TrackPoint a = orderedPoints.get(i - 1);
            TrackPoint b = orderedPoints.get(i);
            total += GeoUtils.distanceMeters(a.getLat(), a.getLng(), b.getLat(), b.getLng());
        }
        return (int) Math.round(total);
    }

    public static String avgPace(int totalTimeSec, int distanceMeters) {
        if (distanceMeters <= 0 || totalTimeSec <= 0) {
            return "00:00";
        }
        double secPerKm = totalTimeSec / (distanceMeters / 1000.0);
        int min = (int) (secPerKm / 60);
        int sec = (int) Math.round(secPerKm - min * 60.0);
        if (sec == 60) {
            min++;
            sec = 0;
        }
        return String.format("%02d:%02d", min, sec);
    }
}
