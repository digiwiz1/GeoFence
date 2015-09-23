package com.digiwiz.geofence;

import com.digiwiz.geofence.log.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by digiwiz on 20-8-2015.
 */
public class httpRequest {

    public static StringBuffer request(String sUrl) {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuffer response = new StringBuffer("");

        //Return empty response
        if (sUrl == null || sUrl.isEmpty())
            return response;

        try {
            url = new URL(sUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) { //generic catch of all Exceptions
            Log.e(Constants.LOG_TAG, "HTTP Error: " + e.getMessage());
        } finally {
            //Disconnect when not null
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        //Return the HTTP response
        return response;
    }
}
