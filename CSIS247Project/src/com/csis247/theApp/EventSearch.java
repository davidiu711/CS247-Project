package com.csis247.theApp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class EventSearch extends Activity {

    /** the drop down mennu to choose a location */
    private Spinner where;

    /** indicates whether the user decided to enter extra information */
    private boolean whereOther = false;

    /** drop down menu for choosing the search radius*/
    private Spinner distance;

    /** indicates whether the users decided to enter their own distance */
    private boolean distanceOther = false;

    /** gives directions for optional location input */
    TextView specifyLocation;

    /** optional location input */
    EditText editLocation;

    /** gives directions for optional distance input */
    TextView specifyDistance;

    /** option distance input */
    EditText editDistance;

    /** the application context */
    private Context context;

    /** widgets for displaying and picking time */
    private TextView mTimeDisplay;
    private Button mPickTime;

    /** integers representing the hour and minute*/
    private int mHour;
    private int mMinute;

    /** widgets for displaying and picking the date */
    private TextView mDateDisplay;
    private Button mPickDate;

    /** integers relating to the date */
    private int mYear;
    private int mMonth;
    private int mDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.eventsearch);

        context = getApplicationContext();
        
        intitalizeWidgets();

        

    }

    /** find and set widgets in this view */
    private void intitalizeWidgets() {

        /* initializes four widgets that deal with optional location and distance input*/
        specifyLocation = (TextView) findViewById(R.id.Text_Search_Specify_Location);

        editLocation = (EditText) findViewById(R.id.Edit_Search_Specify_Location);

        specifyDistance = (TextView) findViewById(R.id.Text_Search_Specify_Distance);

        editDistance = (EditText) findViewById(R.id.Edit_Search_Specify_Distance);

        
        
        /* set up location choice spinner */
        where = (Spinner) findViewById(R.id.Spinner_Location);
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(
                        this, R.array.location_array, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        where.setAdapter(locationAdapter);
        where.setOnItemSelectedListener(new LocationSelectedListener());

        /*set up distance/radius choice spinner and account for miles or kilometers */
        distance = (Spinner) findViewById(R.id.Spinner_Distance);
        ArrayAdapter<CharSequence> distanceAdapter;

        SharedPreferences count = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String country =  count.getString("country", "PROBLEM");
        Log.d("EventSearch", "Country: " + country);
        if (country.equals("USA") || country.equals("GBR")) {
            distanceAdapter = ArrayAdapter.createFromResource(
                            this, R.array.distance_array, android.R.layout.simple_spinner_item);
        } else {
            distanceAdapter = ArrayAdapter.createFromResource(
                            this, R.array.distance_array_km, android.R.layout.simple_spinner_item);
        }

        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance.setAdapter(distanceAdapter);
        distance.setOnItemSelectedListener(new DistanceSelectedListener());

        /* textview and button for displaying and picking time */
        mTimeDisplay = (TextView) findViewById(R.id.Text_Event_Search_timeDisplay);
        mPickTime = (Button) findViewById(R.id.Button_Event_Search_pickTime);

        /* define what happens when pick time button is pressed */
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });


        /* textview and button for displaying and picking date */
        mDateDisplay = (TextView) findViewById(R.id.Text_Event_Search_dateDisplay);
        mPickDate = (Button) findViewById(R.id.Button_Event_Search_pickDate);

        /* define what happens when pick date button is pressed */
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(1);
            }
        });

        /* this is the "submit" button. when it is pressed the listView activity is launched.
         */
        Button findEvents = (Button) findViewById(R.id.Button_Event_Search_findEvents);
        findEvents.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO store inputed values in sharedPreferences so that they can be used in the search query.

                SharedPreferences searchCriteria = context.getSharedPreferences("search", Context.MODE_PRIVATE);
                Editor searchEdit = searchCriteria.edit();

                if (whereOther == true) {
                    searchEdit.putString("location", editLocation.getText().toString());
                } else {
                    searchEdit.putString("location", "Current_Location");
                }
                
                if (distanceOther == true) {
                    searchEdit.putString("distance", editDistance.getText().toString());
                }


                searchEdit.putString("time", mTimeDisplay.getText().toString());
                searchEdit.putString("date", mDateDisplay.getText().toString());
                                

                Intent eventList = new Intent(context, EventList.class);
                startActivity(eventList);

            }
        });

        /* define the current date and time */
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);         

        // display the current time and date
        updateDisplay();

    }

    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
                    new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            updateDisplay();
        }
    };

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, 
                        int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };

    /* defines what dialog gets set depending on if the time or date button was pressed */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case 0:
            return new TimePickerDialog(this,
                            mTimeSetListener, mHour, mMinute, DateFormat.is24HourFormat(context));
        case 1:
            return new DatePickerDialog(this,
                            mDateSetListener,
                            mYear, mMonth, mDay);
        }
        return null;
    }

    /* updates the time or date to reflect the most currently chosen values */
    private void updateDisplay() {
        mTimeDisplay.setText(
                        new StringBuilder()
                        .append(pad(mHour)).append(":")
                        .append(pad(mMinute)));

        //TODO change date format based on region. get rid of time.

        StringBuilder string = new StringBuilder()
        // Month is 0 based so add 1
        .append(mMonth + 1).append("-")
        .append(mDay).append("-")
        .append(mYear);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");

        try {               
            Date date = format.parse(string.toString());
            mDateDisplay.setText(date.toLocaleString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

    }

    // used for formating the time 
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    /* defines what happened when a location spinner item is selected. New
     *  views will appear as needed if extra information is required.
     */
    private class LocationSelectedListener implements OnItemSelectedListener {




        public void onItemSelected(AdapterView<?> parent,
                        View view, int pos, long id) {

            if (parent.getItemAtPosition(pos).toString().equals("Other")) {
                specifyLocation.setVisibility(View.VISIBLE);
                editLocation.setVisibility(View.VISIBLE);
                specifyLocation.setText(R.string.Search_Specify_Location);
                whereOther = true;
            } else if (parent.getItemAtPosition(pos).toString().equals("A Specific Address")) {
                specifyLocation.setVisibility(View.VISIBLE);
                editLocation.setVisibility(View.VISIBLE);

                specifyLocation.setText(R.string.Search_Specify_Address);
                whereOther = true;
            } else {
                specifyLocation.setVisibility(View.GONE);
                editLocation.setVisibility(View.GONE);
                whereOther = false;
            }
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }

    }

    /* defines what happened when a distance spinner item is selected. New
     *  views will appear as needed if extra information is required.
     */
    private class DistanceSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                        View view, int pos, long id) {

            SharedPreferences searchCriteria = context.getSharedPreferences("search", Context.MODE_PRIVATE);

            if (parent.getItemAtPosition(pos).toString().equals("Other")) {
                specifyDistance.setVisibility(View.VISIBLE);
                editDistance.setVisibility(View.VISIBLE);
                
                SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
                String country =  prefs.getString("country", "PROBLEM");
                Log.d("EventSearch", "Country: " + country);
                String units;
                if (country.equals("USA") || country.equals("GBR")) {
                    units = " miles.";
                } else {
                units = " kilometers.";
                }
                specifyDistance.setText(getString(R.string.Search_Specify_Distance) + units);
                distanceOther = true;
            } else {
                specifyDistance.setVisibility(View.GONE);
                editDistance.setVisibility(View.GONE);
                distanceOther = false;
            }

            Editor searchEdit = searchCriteria.edit();
            searchEdit.putString("distance", parent.getItemAtPosition(pos).toString());
            searchEdit.commit();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }

    }
}
