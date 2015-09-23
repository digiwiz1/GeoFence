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

package com.ruud.geofencehome;

public final class Constants {

    public static final String KEY_POWER_MODE = "com.ruud.geofencehome.KEY_POWER_MODE";
    public static final String KEY_UPDATE_INTERVAL = "com.ruud.geofencehome.KEY_UPDATE_INTERVAL";

    public static final String KEY_SHOW_COORDINATES_NOTIFICATION = "com.ruud.geofencehome.KEY_SHOW_COORDINATES_NOTIFICATION";

    public static final String KEY_SHOW_GEOFENCE_NOTIFICATION = "com.ruud.geofencehome.KEY_SHOW_GEOFENCE_NOTIFICATION";

    public static final String KEY_ENTER_GEOFENCE_URL = "com.ruud.geofencehome.KEY_ENTER_GEOFENCE_URL";
    public static final String KEY_EXIT_GEOFENCE_URL = "com.ruud.geofencehome.KEY_EXIT_GEOFENCE_URL";


    private Constants() {
    }

    public static final String LOG_TAG = "GeoFenceHome";

    // Keys for geofence data stored in SharedPreferences.
    public static final String KEY_LATITUDE = "com.ruud.geofencehome.KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "com.ruud.geofencehome.KEY_LONGITUDE";
    public static final String KEY_RADIUS = "com.ruud.geofencehome.KEY_RADIUS";

    public static final String KEY_TRACK_COORDINATES = "com.ruud.geofencehome.KEY_TRACK_COORDINATES";
}
