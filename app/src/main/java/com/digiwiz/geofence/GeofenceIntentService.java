package com.digiwiz.geofence;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;

import com.digiwiz.geofence.log.Log;
import com.digiwiz.geofence.settings.SimpleGeofenceStore;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.DateFormat;
import java.util.Date;


public class GeofenceIntentService extends IntentService /*implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener */ {
    public static final String TRANSITION_INTENT_SERVICE = "GeofenceTransitionsIntentService";

    public GeofenceIntentService() {
        super(TRANSITION_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);

        if (geoFenceEvent.hasError()) {
            Log.e(Constants.LOG_TAG, "Location Services error: " + geoFenceEvent.getErrorCode());
        } else {

            //Get location where geofence transition was triggered
            Location triggeringLocation = geoFenceEvent.getTriggeringLocation();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            //Get location of geofence
            Location geoFence = new Location("");
            geoFence.setLatitude(SimpleGeofenceStore.getLatitude());
            geoFence.setLongitude(SimpleGeofenceStore.getLongitude());

            switch (geoFenceEvent.getGeofenceTransition()) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    //Call Geofence Enter URL
                    StringBuffer response = httpRequest.request(SimpleGeofenceStore.getGeoFenceEnterUrl());

                    if (SimpleGeofenceStore.getGeofenceShowInLog()) {
                        String contentText = currentDateTimeString
                                + ": Geofence entered ("
                                + convertCoordinate.convertLatitude(triggeringLocation.getLatitude())
                                + " "
                                + convertCoordinate.convertLongitude(triggeringLocation.getLongitude())
                                + " Accuracy:"
                                + Math.round(triggeringLocation.getAccuracy()) + "m"
                                + " Distance: " + Math.round(triggeringLocation.distanceTo(geoFence)) + "m)";
                        Log.i(Constants.LOG_TAG, contentText);

                        //Log HTTP error response
                        if (response.length() > 0)
                            Log.i(Constants.LOG_TAG, "HTTP Error:" + response);

                    }

                    if (SimpleGeofenceStore.getGeofenceNotify()) {
                        // Since we only support 1 geofence we can get the first geofence id triggered.
                        // in some cases you might want to consider the full list
                        // of geofences triggered.
                        String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                                .getRequestId();
                        generateNotification(triggeredGeoFenceId, "Entering", triggeringLocation);
                    }
                    break;

                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    //Call Geofence Exit URL
                    response = httpRequest.request(SimpleGeofenceStore.getGeoFenceExitUrl());

                    if (SimpleGeofenceStore.getGeofenceShowInLog()) {

                        String contentText = currentDateTimeString
                                + ": Geofence exited (:"
                                + convertCoordinate.convertLatitude(triggeringLocation.getLatitude())
                                + " "
                                + convertCoordinate.convertLongitude(triggeringLocation.getLongitude())
                                + " Accuracy:"
                                + Math.round(triggeringLocation.getAccuracy()) + "m"
                                + " Distance: " + Math.round(triggeringLocation.distanceTo(geoFence)) + "m)";
                        Log.i(Constants.LOG_TAG, contentText);

                        //Log HTTP error response
                        if (response.length() > 0)
                            Log.i(Constants.LOG_TAG, "HTTP Error:" + response);
                    }

                    if (SimpleGeofenceStore.getGeofenceNotify()) {
                        String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                                .getRequestId();
                        generateNotification(triggeredGeoFenceId, "Leaving", triggeringLocation);
                    }

                    break;

                default:
                    break;
            }
        }
    }

    private void generateNotification(String locationId, String notifyText, Location triggeringLocation) {

        String contentText = convertCoordinate.convertLatitude(triggeringLocation.getLatitude())
                + " "
                + convertCoordinate.convertLongitude(triggeringLocation.getLongitude())
                + " ("
                + Math.round(triggeringLocation.getAccuracy()) + "m)";

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(locationId + " " + notifyText)
                        .setContentText(contentText)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setWhen(System.currentTimeMillis());

        //Get the notificationManager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Send notification- always with ID=1 so it will update existing notifications
        notificationManager.notify(1, builder.build());

    }

}
