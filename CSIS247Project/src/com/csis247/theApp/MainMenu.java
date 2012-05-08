package com.csis247.theApp;

import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainMenu extends Activity implements OnClickListener {
    
    Context context;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d("MainMenu", "In onCreate");
        context = getApplicationContext();
        
        setContentView(R.layout.main);
        
        intitalizeWidgets();
        
        setLocale();
        
        Log.d("GPS", "Start GPS Service");
        /*start GPS service. This service listens to the GPS and broadcasts updates
         * containing the latitude and longitude of the phone's location.
         */
        ComponentName comp = new ComponentName(
                this.getApplicationContext().getPackageName(), 
                GpsService.class.getName());
        this.getApplicationContext().startService(new Intent().setComponent(comp));
        
    }
    
    /** find and set widgets for this view to be clickable */
    private void intitalizeWidgets() {
        ImageButton events = (ImageButton) findViewById(R.id.button_eventlist);
        events.setOnClickListener(this);
        
        ImageButton createEvent = (ImageButton) findViewById(R.id.button_createEventActivity);
        createEvent.setOnClickListener(this);
        
    }
    
    /* sets the default country of the device into shared preferences for future use */
    private void setLocale() {
        SharedPreferences locale = getApplicationContext().getSharedPreferences("locale", Context.MODE_PRIVATE);
        Editor edit = locale.edit();
        
        edit.putString("country", Locale.getDefault().getISO3Country());
        edit.commit();
    }

    /** There are two buttons on the main screen. This method starts the activities
     * associated with those two buttons.
     */
    @Override
    public void onClick(View v) {
        switch( v.getId()){
            case R.id.button_eventlist:
                
                Intent eventSearch = new Intent(this, EventSearch.class);
                startActivity(eventSearch);
                break;
            case R.id.button_createEventActivity:
                Intent createEvent = new Intent (this, CreateEvent.class);
                startActivity(createEvent);
                break;
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