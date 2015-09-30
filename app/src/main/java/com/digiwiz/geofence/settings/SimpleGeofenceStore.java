/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digiwiz.geofence.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.digiwiz.geofence.Constants;
import com.digiwiz.geofence.log.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;


/**
 * Storage for geofence values, implemented in SharedPreferences.
 */
public class SimpleGeofenceStore {
    // The SharedPreferences object in which geofences are stored.
    private final SharedPreferences mPrefs;

    /**
     * Create the SharedPreferences storage with private access only.
     */
    public SimpleGeofenceStore(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private Geofence getGeofence() {
        double lat;
        double lng;
        float radius;
        int responseNotification;

        String value = mPrefs.getString(Constants.KEY_LATITUDE, "");
        if (!value.isEmpty()) {

            try {
                lat = Double.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for latitude has an invalid format");
                return null;
            }
        } else return null;


        value = mPrefs.getString(Constants.KEY_LONGITUDE, "");
        if (!value.isEmpty()) {
            try {
                lng = Double.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for longitude has an invalid format");
                return null;
            }
        } else return null;

        value = mPrefs.getString(Constants.KEY_RADIUS, "");
        if (!value.isEmpty()) {
            try {
                radius = Float.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for radius has an invalid format");
                return null;
            }
        } else return null;

        value = mPrefs.getString(Constants.KEY_RESPONSIVENESS, "");
        if (!value.isEmpty()) {
            try {
                responseNotification = Integer.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for notification response has an invalid format");
                return null;
            }
        } else return null;

        // Build a new Geofence object.
        return new Geofence.Builder()
                .setRequestId("Home")
                .setCircularRegion(lat, lng, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        //.setNotificationResponsiveness(responseNotification * 1000)
                .build();
    }

    // Instance field getters.
    public double getLatitude() {
        String value = mPrefs.getString(Constants.KEY_LATITUDE, "");
        if (!value.isEmpty()) {

            try {
                return Double.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for latitude has an invalid format");
                return 0;
            }
        } else return 0;
    }

    public double getLongitude() {
        String value = mPrefs.getString(Constants.KEY_LONGITUDE, "");
        if (!value.isEmpty()) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for longitude has an invalid format");
                return 0;
            }
        } else return 0;
    }

    public float getRadius() {
        String value = mPrefs.getString(Constants.KEY_RADIUS, "");
        if (!value.isEmpty()) {
            try {
                return Float.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for radius has an invalid format");
                return 0;
            }
        } else return 0;
    }

    public int getPowerMode() {
        String value = mPrefs.getString(Constants.KEY_POWER_MODE, "102");

        if (!value.isEmpty()) {
            try {
                return Math.round(Integer.valueOf(value));
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for location power mode has an invalid format");
                return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
            }
        } else return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    }

    public int getLocationUpdateInterval() {
        String value = mPrefs.getString(Constants.KEY_UPDATE_INTERVAL, "30");
        if (!value.isEmpty()) {
            try {
                return Integer.valueOf(value) * 1000;
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for update interval has an invalid format");
                return 0;
            }
        } else return 0;
    }

    public int getFastestLocationUpdateInterval() {
        String value = mPrefs.getString(Constants.KEY_FASTEST_UPDATE_INTERVAL, "30");
        if (!value.isEmpty()) {
            try {
                return Integer.valueOf(value) * 1000;
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for fastest update interval has an invalid format");
                return 0;
            }
        } else return 0;
    }


    /**
     * Check if geofence notifications are requested
     *
     * @return True - Notification enabled
     */
    public boolean getGeofenceNotify() {
        return mPrefs.getBoolean(Constants.KEY_SHOW_GEOFENCE_NOTIFICATION, true);
    }

    /**
     * Check if active location polling is requested
     *
     * @return True - Active polling enabled
     */
    public boolean getLocationActivePolling() {
        return mPrefs.getBoolean(Constants.KEY_POLL_COORDINATES, false);
    }

    /**
     * Check if user wants location updates to be logged
     *
     * @return True - Log the location updates
     */
    public boolean getLocationShowInLog() {
        return mPrefs.getBoolean(Constants.KEY_SHOW_COORDINATES, false);
    }

    /**
     * Check if location update notifications are requested
     *
     * @return True - Notification enabled
     */
    public boolean getLocationNotify() {
        return mPrefs.getBoolean(Constants.KEY_NOTIFY_COORDINATES, false);
    }


    public String getGeoFenceEnterUrl() {
        return mPrefs.getString(Constants.KEY_ENTER_GEOFENCE_URL, "");
    }

    public String getGeoFenceExitUrl() {
        return mPrefs.getString(Constants.KEY_EXIT_GEOFENCE_URL, "");
    }


    public GeofencingRequest getGeofencingRequests() {
        ArrayList<Geofence> geofenceList = new ArrayList<>();

        Geofence geofence = getGeofence();
        if (geofence == null)
            return null;

        geofenceList.add(geofence);

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT | GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

}
