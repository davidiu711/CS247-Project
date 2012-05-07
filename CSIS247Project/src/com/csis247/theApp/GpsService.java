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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

//TODO store preference for location update interval and use it.

public class GpsService extends Service {

    /**location mananger for the GPS */
    private LocationManager locationManager;

    /** the class that has the GPS framework */
    GPSListener gpsListener;

    /** class that houses the network listner framework */
    NetworkListener networkListener;

    /** a boolean that indicates if the service just started */
    boolean start = true;

    /** a location object to store the most recent gps location */
    Location gps = null;

    /** a location object to store the most recent network location */
    Location network = null;




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

        if (networkListener == null) {
            networkListener = new NetworkListener();
            networkListener.start();
        }

        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    /** GPSListener provides a framework for receiving GPS location updates */
    private class GPSListener implements LocationListener {

        private boolean isRunning;


        /* start the GPS to listen for location updates */
        public void start() {
            Log.d("GpsService", "Start GPS");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, this);
            isRunning = true;
        }

        /* stop the GPs for listening for location updates */
        public void stop() {
            Log.d("GpsService", "Stop GPS");
            locationManager.removeUpdates(this);
            isRunning = false;
        }

        @Override
        public void onLocationChanged(Location l) {
            Log.d("GpsService", " GPS OnLocationChange");
            locationChanged(l);
        }

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("GpsService", " GPS provider disabled");
            // TODO tell user that GPS is disabled with notification.
        }

        @Override
        public void onProviderEnabled(String provider) {
            this.start();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    
    /** NetworkListener provides a framework for receiving network location updates */
    private class NetworkListener implements LocationListener {
        private boolean isRunning;
        private long lastSwitchedOn;

        public void start() {
            Log.d("GpsService", "Starting Network Provider");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
                            200, 0, this);
            isRunning = true;
            this.lastSwitchedOn = System.currentTimeMillis();
        }

        public void stop() {
            Log.d("GpsService", "Stopping Network Provider");
            locationManager.removeUpdates(this);
            isRunning = false;
        }

        public void onLocationChanged(Location location) {
            Log.d("GpsService", "Network Provider location changed");
            locationChanged(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("GpsService", "Network Provider Disabled");
        }
        @Override
        public void onProviderEnabled(String provider) {
            this.start();
            isRunning = true;
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public boolean isRunning() {
            return this.isRunning;
        }

        public long getLastSwitchedOn() {
            return this.lastSwitchedOn;
        }
    }

    private void locationChanged(Location l) {
        Log.d("GpsService","Location Changed ");
        Log.d("GpsService","Provide is " + l.getProvider());
        Log.d("GpsService","Accuracy is " + l.getAccuracy());

        //if the service just started immediately save the first location point that comes in.
        if (start) {
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
            start = false;
        }

        /* update the latest network provided location and start waiting for 30 seconds
         * for the GPS to provide a location update. After 30 seconds turn off everything.
         */
        if (l.getProvider().equals("network")) {
            if (gps != null && gps.getAccuracy() == 100000) {
                gps = null;
            }
            network = l;
            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                public void run() {
                    gps = new Location(LocationManager.GPS_PROVIDER);
                    gps.setAccuracy(100000);
                    locationChanged(gps);
                }
            }, 30000);

            //update the latest GPS provided location
        } else {
            gps = l;
        }

        /* when a point from each provider has been collected, compare their accuracies then
         *  store and broadcast the more accurate location info. Afterwards turn everything off and wait 
         *  for the specified time to turn the listeners on again.
         */
        if (gps != null && network != null) {
            if (gps.getAccuracy() < network.getAccuracy()) {
                Log.d("GpsService","GPS is more accurate");
                //save location in shared preferences for Event class to access when it is created.
                Context context = getApplicationContext();
                SharedPreferences location = context.getSharedPreferences("location", Context.MODE_PRIVATE);
                Editor locationEditor = location.edit();
                locationEditor.putFloat("lat", (float) gps.getLatitude());
                locationEditor.putFloat("lon", (float) gps.getLongitude());
                locationEditor.commit();

                //Send a broadcast message with location change details
                Intent i = new Intent("com.csis247.theApp.customLocationUpdate");
                i.putExtra("lat", gps.getLatitude());
                i.putExtra("lon", gps.getLongitude());
                this.getApplicationContext().sendBroadcast(i);
            } else {
                Log.d("GpsService","Network is more accurate");
                //save location in shared preferences for Event class to access when it is created.
                Context context = getApplicationContext();
                SharedPreferences location = context.getSharedPreferences("location", Context.MODE_PRIVATE);
                Editor locationEditor = location.edit();
                locationEditor.putFloat("lat", (float) network.getLatitude());
                locationEditor.putFloat("lon", (float) network.getLongitude());
                locationEditor.commit();

                //Send a broadcast message with location change details
                Intent i = new Intent("com.csis247.theApp.customLocationUpdate");
                i.putExtra("lat", network.getLatitude());
                i.putExtra("lon", network.getLongitude());
                this.getApplicationContext().sendBroadcast(i);
            }
            gps = null;
            network = null;
            gpsListener.stop();
            networkListener.stop();
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    gpsListener.start();
                    networkListener.start();
                }
            }, 300000); 

        }
    }
}
