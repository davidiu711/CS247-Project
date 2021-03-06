package com.csis247.theApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

/** RefreshEvents is an Asynchronous task that will download events
 *  from the server.*/
public class RefreshEvents extends AsyncTask<Void, Void, Void>{

    /** the context of the activity that called invoked this class.*/
    private Context callingContext;
    
    /** the Activity that invoked this class.*/
    private Activity callingActivity;
    
    /** a dialog displayed to the user to signify that some sort of 
     * loading action is occurring. */
    private ProgressDialog progressDisplay;
    
    
    public RefreshEvents(Context context, Activity activity) {
        callingContext = context;
        
        callingActivity = activity;
    }
    
    
    @Override
    protected void onPreExecute() {
        
        /* delete previously stored events */
        int prefNum = callingContext.getSharedPreferences("numberOfEvents", Context.MODE_PRIVATE).getInt("number", 0);
        for (int i = 0; i < prefNum; i += 1) {
            callingContext.getSharedPreferences(Integer.toString(i), Context.MODE_PRIVATE).edit().clear().commit();
        }

        /*show a dialog message that says "Loading..." while the
         * events are being downloaded. */
        progressDisplay = new ProgressDialog(callingActivity);
        progressDisplay.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDisplay.setMessage(callingContext.getResources().getText(R.string.Refresh_Events_Dialog));
        progressDisplay.show();

    }
    
    @Override
    protected Void doInBackground(Void... arg0) {
           
        /* create an instance of the communicator class and call the communicate method.
         * this method talks to the server. */
        Communicator communicator = new Communicator(progressDisplay, callingContext, callingActivity);
        communicator.communicate();
        
        return null;
    }
}
