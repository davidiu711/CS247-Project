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

        //here are the lat long coordinates to send to the server.
        SharedPreferences currentLocation = callingContext.getSharedPreferences("location", Context.MODE_PRIVATE);
        double lat = currentLocation.getFloat("lat", 0);
        double lon = currentLocation.getFloat("lon", 0);

        

        //TODO talk to the server, load the downloaded information into shared preferences. Use storeDataInSharedPreferences in loop.
        //use lat, lon class variables as the user's location to send to the server.


        /*
         * For (total number of events) { 
        SharedPreferences sharedPreferences = callingContext.getSharedPreferences(PUT LOOP NUMBER HERE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("ID", 0);
        editor.putString("title", "B-Day Party"); <--change these abritarty values
        editor.putString("description", "It's a birthday party");
        editor.putString("time", "7pm");
        editor.putString("distance", "1 mile");

        //TODO want to also put the lat and lon from the downloaded event into the shared preferences via the editor.

        editor.commit();
        }*/

        /*after receiving and processing events, load the numberOfEvents shared preference with the total number of events downloaded.*/
        SharedPreferences sharedPreferences = callingContext.getSharedPreferences("numberOfEvents", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //the number 1 is a placeholder for debugging. replace with the total number of events.
        editor.putInt("number", 1);
        editor.commit();


        finishUp();
    }

    /*finish up stops the progress dialog, and restarts the EventList activity. */
    private void finishUp() {

        activeProgressDialog.dismiss();

        /* The reason there isn't an infinite loop is because in the Manifest the
         * EventList Activity has a launchMode=singleTask attribute.
         */
        callingActivity.startActivity(new Intent(callingContext,
                        EventList.class));
    }
}
