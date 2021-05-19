package com.macinternetservices.sablebusinessdirectory.utils;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.macinternetservices.sablebusinessdirectory.MainActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GeofenceReceiver extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static int near = 0;

    public GeofenceReceiver() {
        super("GeofenceReceiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geoEvent = GeofencingEvent.fromIntent(intent);
        if (geoEvent.hasError()) {
            Log.e("GeoEventErro", "There was a geo even error");
        } else {

            int transitionType = geoEvent.getGeofenceTransition();
            near = 0;
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER
                    || transitionType == Geofence.GEOFENCE_TRANSITION_DWELL
                    || transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> triggerList = geoEvent.getTriggeringGeofences();
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                //preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                gpsTracker.getLocation();

                SimpleGeofence sg = null;
                for (SimpleGeofence simpleGeofenceHashMap  : GeolocationService.geofences.values()) {
                    sg = simpleGeofenceHashMap;
                    String transitionName = "";
                    switch (transitionType) {
                        case Geofence.GEOFENCE_TRANSITION_DWELL:
                            if (distance(gpsTracker.latitude, gpsTracker.longitude,
                                    simpleGeofenceHashMap.getLatitude(), simpleGeofenceHashMap.getLongitude()) <= Double.parseDouble(Constants.CONST_RADIUS) && simpleGeofenceHashMap.getIsPromoted().equals("1")) { // if distance < 5 miles and isPromoted alert
                                transitionName = "dwell";
                                break;
                            }
                        case Geofence.GEOFENCE_TRANSITION_ENTER:
                            if (distance(gpsTracker.latitude, gpsTracker.longitude,
                                    simpleGeofenceHashMap.getLatitude(), simpleGeofenceHashMap.getLongitude()) <= Double.parseDouble(Constants.CONST_RADIUS) && simpleGeofenceHashMap.getIsfeatured().equals("1")) { // if distance > 3 miles alert
                                transitionName = "enter";
                                near++;
                            }
                            break;
                        case Geofence.GEOFENCE_TRANSITION_EXIT:
                            if (distance(gpsTracker.latitude, gpsTracker.longitude,
                                    simpleGeofenceHashMap.getLatitude(), simpleGeofenceHashMap.getLongitude()) >= Double.parseDouble(Constants.CONST_RADIUS) && simpleGeofenceHashMap.getIsfeatured().equals("1")) { // if distance > 3 miles alert
                                transitionName = "exit";
                                near++;
                            }
                            break;
                    }
                }
                //if (near > 0) {
                    GeofenceNotification geofenceNotification = new GeofenceNotification(
                            this);
                    try {
                        geofenceNotification
                                .displayNotification(sg, transitionType, near);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), "There are " + near + " black owned businesses near your current location", Toast.LENGTH_LONG).show();
                //}
            }
        }
    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist; // output distance, in MILES
    }
}




