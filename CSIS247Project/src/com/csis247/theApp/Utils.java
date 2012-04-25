package com.csis247.theApp;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Utils {

    /* a function to get the distance between two lat/lon pairs */
    public static double GetDistanceFromLatLon(double lat1, double lon1, double lat2, double lon2) {

        int R = 6378; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * 
            Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (R * c) * 1000;
    }
    
    
    /* for talking to the server. If you want to use an http request. */
    public static String getData(String endpoint) 
        throws ClientProtocolException, IOException, NumberFormatException {
        
        String responseBody = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(endpoint);    
    
        // Execute HTTP Get Request
        HttpResponse response = httpclient.execute(httpget);
        responseBody = EntityUtils.toString(response.getEntity());      
        
        return responseBody;
    }
    
}
