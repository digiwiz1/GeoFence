package com.digiwiz.geofence;

import org.apache.http.HttpStatus;
import org.apache.http.impl.EnglishReasonPhraseCatalog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by digiwiz on 20-8-2015.
 */
public class httpRequest {

    public static StringBuffer request(String sUrl) {
        URL url;
        HttpURLConnection urlConnection;
        StringBuffer response = new StringBuffer("");

        //Return empty response
        if (sUrl == null || sUrl.isEmpty()) {
            response.append("No URL specified, skipping");
            return response;
        }


        ///Create URL
        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            response.append("Malformed URL");
            return response;
        }


        //Connect to URL
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e1) {
            response.append("Unable to connect to URL");
            return response;
        }

        //Get error response code
        try {
            if (urlConnection.getResponseCode() != HttpStatus.SC_OK)
                response.append(EnglishReasonPhraseCatalog.INSTANCE.getReason(urlConnection.getResponseCode(), Locale.ENGLISH));
        } catch (IOException e) {
            response.append("Unable to get HTTP response code");
            return response;
        }

        //Read response
        /*try {
        BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            response.append("Error reading response from server");
            return response;
        }
        */

        //Close connection
        // urlConnection.disconnect();

        return response;
    }
}
