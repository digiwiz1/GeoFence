package com.digiwiz.geofence;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.digiwiz.geofence.log.Log;
import com.digiwiz.geofence.settings.SimpleGeofenceStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class LocationIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String LOCATION_UPDATE_INTENT_SERVICE = "LocationIntentService";
    // The SharedPreferences object in which geofences are stored.
    private GoogleApiClient mGoogleApiClient;

    public LocationIntentService() {
        super(LOCATION_UPDATE_INTENT_SERVICE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location triggeringLocation = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        if (SimpleGeofenceStore.getLocationShowInLog()) {
            //Format coordinate to 5 decimals (which represent an accuracy of 1.1 meter. See http://gis.stackexchange.com/questions/8650/how-to-measure-the-accuracy-of-latitude-and-longitude for more info)

            //calc distance to geofence
            Location geoFence = new Location("");
            geoFence.setLatitude(SimpleGeofenceStore.getLatitude());
            geoFence.setLongitude(SimpleGeofenceStore.getLongitude());

            String contentText = currentDateTimeString
                    + ": Location update ("
                    + convertCoordinate.convertLatitude(triggeringLocation.getLatitude())
                    + " "
                    + convertCoordinate.convertLongitude(triggeringLocation.getLongitude())
                    + " Accuracy:"
                    + Math.round(triggeringLocation.getAccuracy())
                    + "m Distance: "
                    + Math.round(triggeringLocation.distanceTo(geoFence))
                    + "m)";
            Log.i(Constants.LOG_TAG, contentText);
        }

        if (SimpleGeofenceStore.getLocationNotify()) {
            generateNotification("Leaving", triggeringLocation);
        }
    }

    private void generateNotification(String address, Location triggeringLocation) {
            long when = System.currentTimeMillis();

        String contentText = convertCoordinate.convertLatitude(triggeringLocation.getLatitude())
                + " "
                + convertCoordinate.convertLongitude(triggeringLocation.getLongitude())
                + " ("
                + Math.round(triggeringLocation.getAccuracy())
                + "m)";

            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Current coordinates")
                            .setContentText(contentText)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setWhen(when);

            //Get the notificationManager
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Send notification- always with ID=2 so it will update existing location notifications
            notificationManager.notify(2, builder.build());
    }


    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}




