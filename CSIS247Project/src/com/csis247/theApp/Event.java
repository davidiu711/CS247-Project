package com.csis247.theApp;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Event extends MapActivity {

	//comments
    /** the title of the event */
    TextView title;
    
    /** the description of the event */
    TextView description;
    
    /** the time of the event */
    TextView time;
    
    /** the distance to the event from the user's location */
    TextView distance;
   
    /** application context*/
    Context context;
    
    /** the map in the display*/
    MapView map;
    
    /** controller for the map*/
    MapController mapController;
    
    /** the user's current latitude */
    double lat;
    
    /** the user's current longitude */ 
    double lon;
    
    SharedPreferences eventInfo;
    
    LocationInfoReceiver locationInfoReceiver = new LocationInfoReceiver();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.event);
        context = getApplicationContext();
        Intent intent = getIntent();
        Bundle extraInfo = intent.getBundleExtra("extraInfo");
        Integer position =  extraInfo.getInt("position");
        
        initializeWidgets();
        
        eventInfo = context.getSharedPreferences(position.toString()
                        , Context.MODE_PRIVATE);
        
        
        
        loadEventInfo(position);
        
        
    }
    
    private void initalizeLocation() {
        SharedPreferences locationInfo = context.getSharedPreferences("location"
                        , Context.MODE_PRIVATE);
        lat = (double) locationInfo.getFloat("lat", 0);
        lon = (double) locationInfo.getFloat("lon", 0);
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        initalizeLocation();
        
        //Register a broadcast receiver to receive location updates from the background service
        IntentFilter intentFilter = new IntentFilter("com.csis247.theApp.customLocationUpdate");
        this.getApplicationContext().registerReceiver(this.locationInfoReceiver, intentFilter);
        
        updateMap();
    }
    
    
     @Override
    protected void onPause() {
        super.onPause();
        
        //Unregister the broadcast receiver
        this.getApplicationContext().unregisterReceiver(this.locationInfoReceiver);
    }
    


    private void initializeWidgets() {
        
        title = (TextView) findViewById(R.id.Text_EventActivity_Title);
        description = (TextView) findViewById(R.id.Text_EventActivity_Description);
        time = (TextView) findViewById(R.id.Text_EventActivity_Time);
        distance = (TextView) findViewById(R.id.Text_EventActivity_Distance);
        map = (MapView) findViewById(R.id.Event_Map);
        map.setBuiltInZoomControls(true);
        mapController = map.getController();
    }
    
    /* based on the position of the event in the EventList, load the correct data from
     * the shared preferences.
     */
    private void loadEventInfo(Integer position) {
       
    	try {
            String link = "http://i.cs.hku.hk/~stlee/gowhere.php";
			JSONArray result = new JSONArray(Utils.getData(link));
			for (int i = 0; i < result.length(); i++) {
				JSONObject row = result.getJSONObject(i);
				SharedPreferences preference = context.getSharedPreferences(Integer.toString(i), Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preference.edit();
				editor.putString("title", row.getString("name"));
				editor.putString("time", row.getString("time"));
				editor.commit();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        title.setText(eventInfo.getString("title", "PROBLEM"));
        description.setText(eventInfo.getString("description", "PROBLEM"));
        time.setText(eventInfo.getString("time", "PROBLEM"));
        distance.setText(eventInfo.getString("distance", "PROBLEM"));
        //will also want to get lat and lon to display the event on the map.
        
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    /* updates the map to display current user location and event location. */
    private void updateMap() {
        
        //clear the current overlays
        map.getOverlays().clear();
        
        //create new point on map and a new overlay item. Should use lat/lon from shared preference. Currently this is for debugging.
        GeoPoint event = new GeoPoint((int) (33.856 * 1E6), (int) (-117.737 * 1E6));
        OverlayItem overlayItem = new OverlayItem(event, eventInfo.getString("title", "you done fucked up"), "");
        
        //create second point on map. this is the user's current location.
        GeoPoint user = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
        OverlayItem overlayItem1 = new OverlayItem(user, "Current Location", "");  
        
        //specify what the icon on the map looks like. This should be changed.
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        
        //load the points on map into itemizedOverlay then add them to the map.
        MapItemizedOverlay itemizedOverlay = new MapItemizedOverlay(drawable);
        itemizedOverlay.addOverlay(overlayItem);
        itemizedOverlay.addOverlay(overlayItem1);
        List<Overlay> mapOverlays = map.getOverlays();
        mapOverlays.add(itemizedOverlay);
        
        //redraw the map with updated points.
        map.invalidate();
    }
    
    /**
     * Receives broadcast messages from the background service
     */
    public final class LocationInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.csis247.theApp.customLocationUpdate".equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    lat = extras.getDouble("lat");
                    lon = extras.getDouble("lon");

                    updateMap();

                } else {
                    Log.e("Communicator", "Received unexpected intent");
                }

            }       
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preferencesmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
        case R.id.prefs:
            Intent i = new Intent(this, Prefs.class);
            startActivity(i);

        }
        return true;
    }
        
}
