package com.csis247.theApp;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

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
    
    /** a broadcast receiver to receive location updates from GpsService. */
    LocationInfoReceiver locationInfoReceiver;

    public Communicator (Dialog dialog, Context context, Activity activity) {

        activeProgressDialog = dialog;
        callingContext = context;
        callingActivity = activity;
        
        locationInfoReceiver = new LocationInfoReceiver();
        
        //Register a broadcast receiver to receive location updates from the background service
        IntentFilter intentFilter = new IntentFilter("com.csis247.theApp.customLocationUpdate");
        callingContext.registerReceiver(this.locationInfoReceiver, intentFilter);

    }

    public void communicate() {

        //TODO talk to the server, load the downloaded information into shared preferences. Use storeDataInSharedPreferences in loop.
        //use lat, lon class variables as the user's location to send to the server.
        
        //For (total number of events) {
        
        //the name passed into storeDataInSharedPreferences should a string representation of an integer.
        storeDataInSharedPreferences("0");
        //}
        
        /*after receiving and processing events, load the numberOfEvents shared preference with the total number of events downloaded.*/
        SharedPreferences sharedPreferences = callingContext.getSharedPreferences("numberOfEvents", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //the number 1 is a placeholder for debugging. replace with the total number of events.
        editor.putInt("number", 1);
        editor.commit();


        finishUp();
    }

    /*finish up unregisters the location listener, stoping the progress dialog, and restarts the EventList acticity. */
    private void finishUp() {
        
        callingContext.unregisterReceiver(this.locationInfoReceiver);
        
        activeProgressDialog.dismiss();

        /* The reason there isn't an infinite loop is because in the Manifest the
         * EventList Activity has a launchMode=singleTask attribute.
         */
        callingActivity.startActivity(new Intent(callingContext,
                        EventList.class));
    }


    private void storeDataInSharedPreferences(String name) {
        SharedPreferences sharedPreferences = callingContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("ID", 0);
        editor.putString("title", "B-Day Party");
        editor.putString("description", "It's a birthday party");
        editor.putString("time", "7pm");
        editor.putString("distance", "1 mile");
        //TODO want to also put the lat and lon from the downloaded event.
        editor.commit();

    }

    /**
     * Receives broadcast messages from the Gps service.
     */
    public final class LocationInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.csis247.theApp.customLocationUpdate".equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    lat = extras.getDouble("lat");
                    lon = extras.getDouble("lon");


                } else {
                    Log.e("Communicator", "Received unexpected intent");
                }

            }       
        }
    }
}
