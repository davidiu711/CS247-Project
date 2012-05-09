package com.csis247.theApp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import org.json.*;


public class Communicator {

    /** user's most recent latitude */
    private double lat;

    /** user's most recent longitude*/
    private double lon;

    /** the currently active progress dialog.*/
    Dialog activeProgressDialog;

    /** the context of the calling activity.*/
    Context callingContext;

    /** the calling activity.*/
    Activity callingActivity;

    public Communicator (Dialog dialog, Context context, Activity activity) {

        activeProgressDialog = dialog;
        callingContext = context;
        callingActivity = activity;

    }
    
    public Communicator(){}

    public void communicate() {

        /*
         * here are the lat long coordinates to send to the server.
         * 
         * 
         */
        SharedPreferences currentLocation = callingContext.getSharedPreferences("location", Context.MODE_PRIVATE);
        double lat = currentLocation.getFloat("lat", 0);
        double lon = currentLocation.getFloat("lon", 0);
        
        /*
         * 
         * If we need to make queries to the server based on location, this is the most current location information.
         * 
         * 
         * I hope that got your attention.
         */
        
        
        
        
        /*
         *
         * 
         * 
         * 
         * you should download the title, description, address, time, date, lat, lon, and country (of the user) of the event from the server.
         * put each of those strings into the shared preference with those key values (lat, lon, time, date, title, etc.)
         */
        JSONArray result = null;

        try {
                    String link = "http://i.cs.hku.hk/~stlee/gowhere_get_event.php";
                    result = new JSONArray(Utils.getData(link));
                    SharedPreferences numberOfEvents = callingContext.getSharedPreferences("numberOfEvents", Context.MODE_PRIVATE);
                    SharedPreferences.Editor numberOfEventsEditor = numberOfEvents.edit();
                    numberOfEventsEditor.putInt("number", result.length());
                    numberOfEventsEditor.commit();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject row = result.getJSONObject(i);
                        SharedPreferences preference = callingContext.getSharedPreferences(Integer.toString(i), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("title", row.getString("name"));
                        editor.putString("time", row.getString("time"));
                        /*
                         * 
                         * 
                         * put more things in here.
                         * 
                         * 
                         */
                        /*this is the distance that I put into shared preferences. It's done. Don't touch please */
                        Double dist = Utils.GetDistanceFromLatLon(lat, lon, Double.parseDouble(row.getString("lat")), Double.parseDouble(row.getString("lon")));              
                        editor.putString("distance", dist.toString());
                        editor.commit();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        /*after receiving and processing events, load the numberOfEvents shared preference with the total number of events downloaded.*/
        SharedPreferences sharedPreferences = callingContext.getSharedPreferences("numberOfEvents", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("number", result.length());
        editor.commit();

        finishUp();
    }

    /*finish up stops the progress dialog, and restarts the EventList activity. */
    private void finishUp() {

        activeProgressDialog.dismiss();

        /* The reason there isn't an infinite loop is because in the Manifest the
         * EventList Activity has a launchMode=singleTask attribute.
         */
        Intent listActivity = new Intent(callingContext,EventList.class);
        listActivity.putExtra("loadingDone", true);
        callingActivity.startActivity(listActivity);
    }
}
