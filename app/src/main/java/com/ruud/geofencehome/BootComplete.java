package com.ruud.geofencehome;

/**
 * Created by Ruud on 20-8-2015.
 */
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, AutoStartUp.class);
            context.startService(serviceIntent);
        }
    }

}
