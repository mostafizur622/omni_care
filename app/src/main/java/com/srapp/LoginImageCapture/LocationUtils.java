package com.srapp.LoginImageCapture;

import android.location.Location;
import android.util.Log;

import java.util.List;

/******
 **** Created By  TANVIR3488 AT 18/8/25 11:49 PM
 ******/


public class LocationUtils {


    public static boolean isInsideGeofence(Location currentLocation, List<Location> geofencePoints) {
        if (currentLocation == null || geofencePoints == null || geofencePoints.size() < 3) {
            return false; // polygon requires at least 3 points
        }

        double x = currentLocation.getLongitude();
        double y = currentLocation.getLatitude();
        Log.e("LocationUtils", "Checking if point (" + geofencePoints.get(2).getLatitude()+ ") is inside geofence.");

        boolean inside = false;
        int n = geofencePoints.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = geofencePoints.get(i).getLongitude();
            double yi = geofencePoints.get(i).getLatitude();
            double xj = geofencePoints.get(j).getLongitude();
            double yj = geofencePoints.get(j).getLatitude();

            boolean intersect = ((yi > y) != (yj > y)) &&
                (x < (xj - xi) * (y - yi) / (yj - yi + 0.0) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    }


    public static boolean isWithinRadius(Location currentLocation, Location loginLocation, float radiusMeters) {
        if (currentLocation == null || loginLocation == null) return false;

        float distance = currentLocation.distanceTo(loginLocation); // built-in Android method
        return distance <= radiusMeters;
    }
}
