package com.digiwiz.geofence;

/**
 * Created by digiwiz on 20-8-2015.
 */

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.digiwiz.geofence.log.Log;
import com.digiwiz.geofence.settings.SimpleGeofenceStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class AutoStartUp extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleApiClient mApiClient;
    private SharedPreferences mPrefs;

    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;

    // Stores the PendingIntent used to request location monitoring.
    private PendingIntent mLocationRequestIntent;

    private SimpleGeofenceStore geoStore;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.geoStore = new SimpleGeofenceStore(this);

        if (!isGooglePlayServicesAvailable()) {
            return;
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        // set listener for shared preferences; when mainactivity changes the prefs the service will reconfigure
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Checks if Google Play services is available.
     *
     * @return true if it is.
     */
    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            Log.e(Constants.LOG_TAG, "Google Play services is unavailable.");
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mGeofenceRequestIntent = getGeofencePendingIntent();
        mLocationRequestIntent = getLocationPendingIntent();

        if (mApiClient.isConnected()) {
            enableLocationTracking();
            addGeoFence();
        } else {
            Log.i(Constants.LOG_TAG, "Googgle Play services not connected");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
        }
    }

    private void reloadGeoFence() {
        //remove geofences from the API
        LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);

        //re-add the geofences
        addGeoFence();
    }

    private void addGeoFence() {
        GeofencingRequest requests = geoStore.getGeofencingRequests();
        if (requests == null) {
            Log.w(Constants.LOG_TAG, "No configuration found. Please configure the geofence first...");
        } else {
            LocationServices.GeofencingApi.addGeofences(mApiClient, requests, mGeofenceRequestIntent);
        }
    }

    /**
     * Create a PendingIntent that triggers GeofenceIntentService when a geofence
     * transition occurs.
     */
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getLocationPendingIntent() {
        Intent intent = new Intent(this, LocationIntentService.class);
        return PendingIntent.getService(this, 1, intent, 0);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        Log.e(Constants.LOG_TAG, "Connection to Google Play services failed with error code " + errorCode);
    }

    private void enableLocationTracking() {

        if (geoStore.getLocationActivePolling()) {
            Log.i(Constants.LOG_TAG, "Active Location polling is enabled");
            LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create();

            //Set the update interval after which a new location request will be issued
            locationRequest.setInterval(geoStore.getLocationUpdateInterval());

            //Set the fastest receive interval for receiving location updates
            locationRequest.setFastestInterval(geoStore.getFastestLocationUpdateInterval());

            locationRequest.setPriority(geoStore.getPowerMode());

            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, mLocationRequestIntent);
        } else {
            Log.i(Constants.LOG_TAG, "Active Location polling is disabled");
            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, mLocationRequestIntent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        enableLocationTracking();
        reloadGeoFence();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

}