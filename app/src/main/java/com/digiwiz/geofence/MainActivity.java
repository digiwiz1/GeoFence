package com.digiwiz.geofence;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.digiwiz.geofence.log.Log;
import com.digiwiz.geofence.log.LogFragment;
import com.digiwiz.geofence.log.LogWrapper;
import com.digiwiz.geofence.log.MessageOnlyLogFilter;
import com.digiwiz.geofence.settings.SettingsActivity;

public class MainActivity extends FragmentActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mPrefs;
    private Intent service;

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

        service = new Intent(getApplicationContext(), AutoStartUp.class);
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

        //Log.i(Constants.LOG_TAG, "init logging");
    }

    //@Override
    public void onResume() {
        super.onResume();

        //Log.i(Constants.LOG_TAG, "onResume");

        // To use the preferences when the activity starts and when the user navigates back from the settings activity.
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        startService(service);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Log.i(Constants.LOG_TAG, "Settings updated");

        stopService(service);
        startService(service);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.i(Constants.LOG_TAG, "onPause");

        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            //Log.i(Constants.LOG_TAG, "Service name:" + service.service.getClassName());

            if (serviceClass.getName().equals(service.service.getClassName())) {
                //Log.i(Constants.LOG_TAG, "FOUND SERVICE");
                return true;
            }
        }
        return false;
    }

}
