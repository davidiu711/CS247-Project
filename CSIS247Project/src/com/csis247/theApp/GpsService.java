package com.csis247.theApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

//TODO add broadcast receiver so that the time between GPs updates can be changed.

public class GpsService extends Service {

    /** the most recent time the GPS was started */
    private long startTime;
    
    /** the most recent time the GPs was stopped */
    private long stopTime; 
    
    /**location mananger for the GPS */
    private LocationManager locationManager;
    
    /** the class that has the GPS framework */
    GPSListener gpsListener;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {       
        super.onStartCommand(intent, flags, startId);
        
        if (gpsListener == null) {
            gpsListener = new GPSListener();
            gpsListener.start();
            
        }
        
        return START_STICKY;
    }

    

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    

    private class GPSListener implements LocationListener {

        
        private boolean isRunning;


        /* start the GPS to listen for location updates */
        public void start() {
            Log.d("GPS", "Start GPS");
            startTime = System.currentTimeMillis();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, this);
            isRunning = true;
        }

        /* stop the GPs for listening for location updates */
        public void stop() {
            Log.d("GPS", "Stop GPS");
            stopTime = System.currentTimeMillis();
            locationManager.removeUpdates(this);
            isRunning = false;
        }
    
        @Override
        public void onLocationChanged(Location l) {
            Log.d("GPS", "OnLocationChange");
            locationChanged(l);
            
            /* start a timer to wait for 5 minutes to start the GPs again */
            if (startTime + 30000 < System.currentTimeMillis()) {
                stop();
                new Timer().run();
            }
            
        }

        
        public boolean isRunning() {
            return isRunning;
        }
        
        @Override
        public void onProviderDisabled(String provider) {
            Log.d("GPS", "provider disabled");
            // TODO tell user that GPS is disabled with notification.

        }

        @Override
        public void onProviderEnabled(String provider) {
            this.start();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

    private void locationChanged(Location l) {

        //save location in shared preferences for Event class to access when it is created.
        Context context = getApplicationContext();
        SharedPreferences location = context.getSharedPreferences("location", Context.MODE_PRIVATE);
        Editor locationEditor = location.edit();
        locationEditor.putFloat("lat", (float) l.getLatitude());
        locationEditor.putFloat("lon", (float) l.getLongitude());
        locationEditor.commit();

        //Send a broadcast message with location change details
        Intent i = new Intent("com.csis247.theApp.customLocationUpdate");
        i.putExtra("lat", l.getLatitude());
        i.putExtra("lon", l.getLongitude());

        this.getApplicationContext().sendBroadcast(i);
    }
    
    /* a separate thread to wait 5 minutes and then restart the GPS */
    private class Timer implements Runnable {

        @Override
        public void run() {
            long startSleepTime = System.currentTimeMillis();
            while (startSleepTime + 300000 > System.currentTimeMillis()) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            gpsListener.start();
        }

    }
}
