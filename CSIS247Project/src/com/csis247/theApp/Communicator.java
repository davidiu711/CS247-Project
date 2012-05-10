package com.csis247.theApp;

import java.io.IOException;
import java.net.URLEncoder;

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
import android.widget.EditText;

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

    public void communicate(EditText eventName, EditText eventDescription, EditText eventAddress, String eventTime, String eventDate) {
    	
    	/*SharedPreferences currentLocation = callingContext.getSharedPreferences("location", Context.MODE_PRIVATE);
        double lat = currentLocation.getFloat("lat", 0);
        double lon = currentLocation.getFloat("lon", 0);
        */
    	try {
    		String link, event_name, event_description, event_address, event_time, event_date, event_lat, event_lon, event_country;
        	event_name = URLEncoder.encode(eventName.getText().toString(), "UTF-8");
    		event_description = URLEncoder.encode(eventDescription.getText().toString(), "UTF-8");
    		event_address = URLEncoder.encode(eventAddress.getText().toString(), "UTF-8");
    		event_time = URLEncoder.encode(eventTime, "UTF-8");
    		event_date = URLEncoder.encode(eventDate, "UTF-8");
    		//event_lat = Double.toString(lat);
    		//event_lon = Double.toString(lon);
    		event_country = "HK";
    		//temp = mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute+":00";
    		//event_datetime = URLEncoder.encode(temp, "UTF-8");
    		//System.out.println("datetime: "+event_date);
    		//link = "http://i.cs.hku.hk/~stlee/gowhere_create_event.php?event_name="+event_name+"&event_description="+event_description+"&event_address="+event_address+"&event_time="+event_time+"&event_date="+event_date+"&event_lat="+event_lat+"&event_lon="+event_lon+"&event_country="+event_country;
    		link = "http://i.cs.hku.hk/~stlee/gowhere_create_event.php?event_name="+event_name+"&event_description="+event_description+"&event_address="+event_address+"&event_time="+event_time+"&event_date="+event_date+"&event_lat="+"&event_country="+event_country;
    		System.out.println("link: "+link);
    		Utils.getData(link);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
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
                        editor.putString("description", row.getString("description"));
                        editor.putString("address", row.getString("address"));
                        editor.putString("time", row.getString("time"));
                        editor.putString("date", row.getString("date"));
                        editor.putString("lat", row.getString("lat"));
                        editor.putString("lon", row.getString("lon"));
                        editor.putString("country", row.getString("country"));
                        
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
