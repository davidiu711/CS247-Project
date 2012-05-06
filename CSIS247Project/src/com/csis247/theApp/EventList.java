package com.csis247.theApp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class EventList extends Activity {

    /** The application context. */
    Context context;

    /** A listview of all events that needs to be populated from shared preferences. */
    ListView events;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.eventlist);

        context = getApplicationContext();

        events = (ListView) findViewById(R.id.Events_ListView);

        //start an asynchronous process to talk to the server and download events.
            new RefreshEvents(context, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refreshmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
        case R.id.refresh_Events:
            new RefreshEvents(context, this).execute();

        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        loadEventList();

    }

    /** Access data from shared preferences about each event downloaded
     * from the server and load that data into the eventlist.
     */
    private void loadEventList() {

        /* get the shared preference containing the the number of events
         * downloaded from the server.
         */
        SharedPreferences numberOfEvents = context.getSharedPreferences("numberOfEvents", Context.MODE_PRIVATE);

        /*listItems is a list of hashmaps. Each hashmap corresponds 
         * to an event to be displayed in the list.
         */
        ArrayList<ConcurrentHashMap<String, String>> listItems 
        = new ArrayList<ConcurrentHashMap<String, String>>();

        //add a new hash map to listItems for each event downloaded
        for (Integer i = 0; i < numberOfEvents.getInt("number", 0); i += 1){

            /*get the sharedPreference corresponding to each event. Key should be
             * the string representation of the Integer i.
             */
            SharedPreferences eventInfo = context.getSharedPreferences(i.toString(), Context.MODE_PRIVATE);

            ConcurrentHashMap<String, String> singleEventMap = new ConcurrentHashMap<String, String>();

            singleEventMap.put("title", eventInfo.getString("title", "PROBLEM"));
            singleEventMap.put("time", eventInfo.getString("time", "PROBLEM"));
            singleEventMap.put("distance", eventInfo.getString("distance", "PROBLEM"));

            listItems.add(singleEventMap);
        }

        /* the setAdapter method takes a list of hashmaps and maps each of the hash 
         * map values to specific fields in the eventlistevent layout 
         * add image to layout*/
        events.setAdapter( new SimpleAdapter(this, listItems, R.layout.eventlistevent
                        , new String[] {"title", "time", "distance"}
        , new int[] {R.id.eventTitle, R.id.eventTime, R.id.eventDistance} ));

        // define what happens when one of the list items is clicked.
        events.setOnItemClickListener(new ClickListener());
    }


    /** ClickListener defines what happens when a list item is clicked.
     *  An the event activity is started and the position of the item on
     *  the list is passed to the Event activity as an extra in the intent.
     */
    private class ClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                        long id) {

            Intent openEvent = new Intent();
            Bundle extraInfo = new Bundle();
            extraInfo.putInt("position", position);
            openEvent.putExtra("extraInfo", extraInfo);
            openEvent.setAction("com.csis247.theApp.Event");
            startActivity(openEvent);

        }       
    }
}
