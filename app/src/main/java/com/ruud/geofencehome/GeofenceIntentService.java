package com.ruud.geofencehome;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.ruud.geofencehome.log.Log;
import com.ruud.geofencehome.settings.SimpleGeofenceStore;

import java.text.DateFormat;
import java.util.Date;


public class GeofenceIntentService extends IntentService /*implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener */{
    public static final String TRANSITION_INTENT_SERVICE = "ReceiveTransitionsIntentService";
    //private GoogleApiClient mGoogleApiClient;
    private SimpleGeofenceStore geoStore;

    public GeofenceIntentService() {
        super(TRANSITION_INTENT_SERVICE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
                */
        geoStore = new SimpleGeofenceStore(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);

        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(Constants.LOG_TAG, "Location Services error: " + errorCode);
        } else {

            int transitionType = geoFenceEvent.getGeofenceTransition();
            Location triggeringLocation = geoFenceEvent.getTriggeringLocation();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            Log.i(Constants.LOG_TAG, currentDateTimeString);

            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:

                    Log.i(Constants.LOG_TAG, " Entering GeoFence");
                    Log.i(Constants.LOG_TAG, " - Latitude:  " + triggeringLocation.getLatitude());
                    Log.i(Constants.LOG_TAG, " - Longitude: " + triggeringLocation.getLongitude());
                    Log.i(Constants.LOG_TAG, " - Accuracy:  " + triggeringLocation.getAccuracy() + " meter(s)");

                    // Since we only support 1 geofence we can get get the first geofence id triggered.
                    // in some cases you might want to consider the full list
                    // of geofences triggered.
                    String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                            .getRequestId();

                    Log.i(Constants.LOG_TAG, "HTTP Response:"+String.valueOf(httpRequest.request(geoStore.getGeoFenceEnterUrl())));

                    generateNotification(triggeredGeoFenceId, "Entering", triggeringLocation);

                    break;

                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Log.i(Constants.LOG_TAG, " Exiting GeoFence");
                    Log.i(Constants.LOG_TAG, " - Latitude:  " + triggeringLocation.getLatitude());
                    Log.i(Constants.LOG_TAG, " - Longitude: " + triggeringLocation.getLongitude());
                    Log.i(Constants.LOG_TAG, " - Accuracy:  " + triggeringLocation.getAccuracy() + " meter(s)");

                    triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                            .getRequestId();

                    Log.i(Constants.LOG_TAG, "HTTP Response:"+String.valueOf(httpRequest.request(geoStore.getGeoFenceExitUrl())));

                    generateNotification(triggeredGeoFenceId, "Leaving", triggeringLocation);

                    break;

                default:break;
            }
        }
    }

    private void generateNotification(String locationId, String notifyText, Location triggeringLocation) {

        SimpleGeofenceStore store = new SimpleGeofenceStore(this);

        if (store.getGeofenceNotify()) {

            long when = System.currentTimeMillis();

            String contentText = "Lat: " + triggeringLocation.getLatitude() + " Long: " + triggeringLocation.getLongitude() +
                    " Acc: " + triggeringLocation.getAccuracy() + " meter(s)";

            Intent notifyIntent = new Intent(this, MainActivity.class);
             notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.dac_logo)
                            .setContentTitle(locationId + " " + notifyText)
                            .setContentText(contentText)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setWhen(when);

            //Get the notificationManager
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Send notification- always with ID=1 so it will update existing notifications
            notificationManager.notify(1, builder.build());
        }
    }

    /*
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    */
}
