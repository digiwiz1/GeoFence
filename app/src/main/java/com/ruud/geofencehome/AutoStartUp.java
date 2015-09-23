package com.ruud.geofencehome;

/**
 * Created by Ruud on 20-8-2015.
 */
import android.app.PendingIntent;
import android.app.Service;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ruud.geofencehome.log.Log;
import com.ruud.geofencehome.settings.SimpleGeofenceStore;

import java.util.ArrayList;

public class AutoStartUp extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener         ,
        SharedPreferences.OnSharedPreferenceChangeListener {


    private GoogleApiClient mApiClient;

    private ArrayList<Geofence> geofenceList;
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

        this.geofenceList = new ArrayList<Geofence>();
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

        // To use the preferences when the activity starts and when the user navigates back from the settings activity.
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        //Log.i(Constants.LOG_TAG, "Start Service");

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
        //Log.e(Constants.LOG_TAG, "Google Play onConnected.");
        mGeofenceRequestIntent = getGeofencePendingIntent();
        mLocationRequestIntent = getLocationPendingIntent();

        if (mApiClient.isConnected()) {
            enableCoordinates();
            addGeoFence();
        } else {
            Log.i(Constants.LOG_TAG, "Googgle Plat services not connected");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            //Log.i(Constants.LOG_TAG, "Removing geofence");
            LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
        }
    }

    public void reloadGeoFence() {
        //Log.i(Constants.LOG_TAG, "Reload geofence");

        //remove the fences from the API
        LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
        //clear the array
        geofenceList.clear();
        //re-add the fences
        addGeoFence();
    }

    public void addGeoFence() {
        Geofence geofence = geoStore.getGeofence();
        if (geofence == null) {
            Log.w(Constants.LOG_TAG, "No configuration found. Please configure the geofence first...");
        } else {
            geofenceList.add(geofence);

            /** Add a geofence ay current location for debug purposes
             *
             */


            double lat = 52.103368;
            double lng = 4.313771;
            float radius = 1000;

            // Build a new Geofence object.
            Geofence debugFence = new Geofence.Builder()
                    .setRequestId("Debug")
                    .setCircularRegion(lat, lng, radius)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build();

            geofenceList.add(debugFence);

            LocationServices.GeofencingApi.addGeofences(mApiClient, geofenceList,
                    mGeofenceRequestIntent);

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

    public void enableCoordinates() {

        if (geoStore.getTracking()) {
            Log.i(Constants.LOG_TAG, "Enable location tracking");
            LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create();

            //Set the update interval after which a new location request will be issued
            locationRequest.setInterval(geoStore.getUpdateInterval());

            //Set the interval for receiving location updates triggered by other applications
            locationRequest.setFastestInterval(60 * 1000); //1 minute (specified in milliseconds)

            locationRequest.setPriority(geoStore.getPowerMode());

            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, mLocationRequestIntent);
        } else {
            Log.i(Constants.LOG_TAG, "Disable location tracking");
            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, mLocationRequestIntent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        enableCoordinates();
        reloadGeoFence();
    }

}