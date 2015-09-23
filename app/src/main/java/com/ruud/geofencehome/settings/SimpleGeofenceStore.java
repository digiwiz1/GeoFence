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

package com.ruud.geofencehome.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.ruud.geofencehome.Constants;
import com.ruud.geofencehome.log.Log;


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


    public Geofence getGeofence() {
        double lat;
        double lng;
        float radius;

        String value = mPrefs.getString(Constants.KEY_LATITUDE, "");
        if (value != null && !value.isEmpty()) {

            try {
                lat = Double.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for latitude has an invalid format");
                return null;
            }
        } else return null;


        value = mPrefs.getString(Constants.KEY_LONGITUDE, "");
        if (value != null && !value.isEmpty()) {
            try {
                lng = Double.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for longitude has an invalid format");
                return null;
            }
        } else return null;

        value = mPrefs.getString(Constants.KEY_RADIUS, "");
        if (value != null && !value.isEmpty()) {
            try {
                radius = Float.valueOf(value);
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for radius has an invalid format");
                return null;
            }
        } else return null;

        // Build a new Geofence object.
        return new Geofence.Builder()
                .setRequestId("Home")
                .setCircularRegion(lat, lng, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    /**
     * Given a Geofence object's ID and the name of a field (for example, KEY_LATITUDE), return
     * the key name of the object's values in SharedPreferences.
     *
     * @param fieldName The field represented by the key.
     * @return The full key name of a value in SharedPreferences.
     */
    private String getGeofenceFieldKey(String fieldName) {
        return "GEOFENCE_" + fieldName;
    }

    // Instance field getters.
    public double getLatitude() {
        String value = mPrefs.getString(Constants.KEY_LATITUDE, "");
        if (value != null && !value.isEmpty()) {

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
        if (value != null && !value.isEmpty()) {
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
        if (value != null && !value.isEmpty()) {
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

        if (value != null && !value.isEmpty()) {
            try {
                return Math.round(Integer.valueOf(value));
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for location power mode has an invalid format");
                return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
            }
        } else return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    }

    public boolean getTracking() {
        return mPrefs.getBoolean(Constants.KEY_TRACK_COORDINATES, false);
    }

    public int getUpdateInterval() {
        String value = mPrefs.getString(Constants.KEY_UPDATE_INTERVAL, "60");
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.valueOf(value) * 1000;
            } catch (NumberFormatException ex) {
                Log.e(Constants.LOG_TAG, "Entered value for update interval has an invalid format");
                return 0;
            }
        } else return 0;
    }

    public boolean getGeofenceNotify() {
        return mPrefs.getBoolean(Constants.KEY_SHOW_GEOFENCE_NOTIFICATION, true);
    }

    public boolean getCoordinatesNotify() {
        return mPrefs.getBoolean(Constants.KEY_SHOW_COORDINATES_NOTIFICATION, true);
    }

    public String getGeoFenceEnterUrl() {
        return mPrefs.getString(Constants.KEY_ENTER_GEOFENCE_URL, "");
    }

    public String getGeoFenceExitUrl() {
        return mPrefs.getString(Constants.KEY_EXIT_GEOFENCE_URL, "");
    }

}
