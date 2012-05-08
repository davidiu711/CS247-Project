package com.csis247.theApp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.preference.PreferenceManager;
import android.util.Log;

//TODO store preference for location update interval and use it.

public class GpsService extends Service {

    /**location manager for the GPS */
    private LocationManager locationManager;

    /** the class that has the GPS framework */
    private GPSListener gpsListener;

    /** class that houses the network listener framework */
    private NetworkListener networkListener;

    /** a boolean that indicates if the service just started */
    private boolean start = true;

    /** a location object to store the most recent gps location */
    private Location gps = null;

    /** a location object to store the most recent network location */
    private Location network = null;

    private boolean gpsFirst = true;
    
    private boolean networkFirst = true;
    
    private NotificationManager notificationManager;
    



    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
            notifyOnLocationDisable();
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
        Log.d("GpsService","Provider is " + l.getProvider());
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
            if (networkFirst) {
            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                public void run() {
                    gps = new Location(LocationManager.GPS_PROVIDER);
                    gps.setAccuracy(100000);
                    locationChanged(gps);
                }
            }, 30000);
            networkFirst = false;
            }

            //update the latest GPS provided location
        } else {
            if (network != null && network.getAccuracy() == 100000) {
                network = null;
            }
            gps = l;
            if (gpsFirst) {
            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                public void run() {
                    network = new Location(LocationManager.NETWORK_PROVIDER);
                    network.setAccuracy(100000);
                    locationChanged(network);
                }
            }, 30000);
            gpsFirst = false;
            }
        }

        /* when a point from each provider has been collected, compare their accuracies then
         *  store and broadcast the more accurate location info. Afterwards turn everything off and wait 
         *  for the specified time to turn the listeners on again.
         */
        if (gps != null && network != null) {
            if (gps.getAccuracy() < network.getAccuracy()) {
                Log.d("GpsService","GPS is more accurate");
                if (gps.getAccuracy() < 100) {
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
                    Log.d("GpsService","GPS accuracy is more than 100m");
                }
            } else {
                Log.d("GpsService","Network is more accurate");
                if (network.getAccuracy() < 100) {
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
                } else {
                    Log.d("GpsService","network accuracy is more than 100m");
                }
            }
            gps = null;
            network = null;
            gpsListener.stop();
            networkListener.stop();
            
            SharedPreferences locationFrequency = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    gpsListener.start();
                    networkListener.start();
                    networkFirst = true;
                    gpsFirst = true;
                }
            }, Integer.parseInt(locationFrequency.getString("locationUpdateFrequency", "3")) * 1000 * 60); 
            Log.d("GpsService","Update Frequency is " + locationFrequency.getString("locationUpdateFrequency", "3") + " minutes");

        }
    }
    
    /**
     * Display a notification to the user if the GPS sensor is switched off
     */
    private void notifyOnLocationDisable() {
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = getResources().getString(R.string.GPS_Notification_Ticker_Text);
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        Context context = getApplicationContext();
        CharSequence contentTitle = getResources().getString(R.string.GPS_Notification_Content_Title);
        CharSequence contentText = getResources().getString(R.string.GPS_Notification_Content_Text);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notificationManager.notify(1, notification);
    }
}
