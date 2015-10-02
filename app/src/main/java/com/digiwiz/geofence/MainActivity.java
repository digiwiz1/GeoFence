package com.digiwiz.geofence;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.digiwiz.geofence.log.Log;
import com.digiwiz.geofence.log.LogFragment;
import com.digiwiz.geofence.log.LogWrapper;
import com.digiwiz.geofence.log.MessageOnlyLogFilter;
import com.digiwiz.geofence.settings.SettingsActivity;

public class MainActivity extends FragmentActivity {

    private Intent service;

    /**
     * Click Handler - This will clear the textview whenever someone clicks it.
     */
    private View.OnClickListener yourClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.log_fragment);
            logFragment.getLogView().clearLog();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu to add it to the action bar
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Starts the Settings activity on top of the current activity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button clearButton = (Button) findViewById(R.id.clearButton);

        clearButton.setOnClickListener(yourClickListener);

        service = new Intent(getApplicationContext(), AutoStartUp.class);

        //Check if service is already running before starting
        if (!isMyServiceRunning(AutoStartUp.class)) {
            Log.i(Constants.LOG_TAG, "Starting Geofence service");
            startService(service);
        }
    }

    /**
     * Create a chain of targets that will receive log data
     */
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();

        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
    }

    @Override
    public void onResume() {
        super.onResume();

        //Enable logging when resuming
        // Log.enableLogging();
    }


    @Override
    protected void onPause() {
        super.onPause();

        //Disable logging when onPaused
        //Log.disableLogging();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
