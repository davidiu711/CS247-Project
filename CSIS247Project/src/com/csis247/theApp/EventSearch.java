package com.csis247.theApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    private Spinner where;
    
    private Spinner distance;
    
    private Context context;
    
    private TextView mTimeDisplay;
    private Button mPickTime;
    private int mHour;
    private int mMinute;

    private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.eventsearch);

        intitalizeWidgets();
        
        context = getApplicationContext();
        
    }

    /** find and set widgets in this view */
    private void intitalizeWidgets() {

        where = (Spinner) findViewById(R.id.Spinner_Location);
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(
            this, R.array.location_array, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        where.setAdapter(locationAdapter);
        where.setOnItemSelectedListener(new LocationSelectedListener());
        
        distance = (Spinner) findViewById(R.id.Spinner_Distance);
        ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(
            this, R.array.distance_array, android.R.layout.simple_spinner_item);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance.setAdapter(distanceAdapter);
        distance.setOnItemSelectedListener(new DistanceSelectedListener());
        
        mTimeDisplay = (TextView) findViewById(R.id.Text_Event_Search_timeDisplay);
        mPickTime = (Button) findViewById(R.id.Button_Event_Search_pickTime);

        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });
        
        
        // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.Text_Event_Search_dateDisplay);
        mPickDate = (Button) findViewById(R.id.Button_Event_Search_pickDate);

        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(1);
            }
        });
        
        Button findEvents = (Button) findViewById(R.id.Button_Event_Search_findEvents);
        findEvents.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent eventList = new Intent(context, EventList.class);
                startActivity(eventList);
                
            }
        });

        
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private class LocationSelectedListener implements OnItemSelectedListener {

        TextView specifyLocation = (TextView) findViewById(R.id.Text_Search_Specify_Location);

        EditText editLocation = (EditText) findViewById(R.id.Edit_Search_Specify_Location);


        public void onItemSelected(AdapterView<?> parent,
                        View view, int pos, long id) {
            if (parent.getItemAtPosition(pos).toString().equals("Other")) {
                specifyLocation.setVisibility(View.VISIBLE);
                editLocation.setVisibility(View.VISIBLE);
                specifyLocation.setText(R.string.Search_Specify_Location);
            } else if (parent.getItemAtPosition(pos).toString().equals("A Specific Address")) {
                specifyLocation.setVisibility(View.VISIBLE);
                editLocation.setVisibility(View.VISIBLE);
                
                specifyLocation.setText(R.string.Search_Specify_Address);
            } else {
                    specifyLocation.setVisibility(View.GONE);
                    editLocation.setVisibility(View.GONE);
            }
            
            /*SharedPreferences searchCriteria = context.getSharedPreferences("search", Context.MODE_PRIVATE);
            Editor searchEdit = searchCriteria.edit();
            searchEdit.putString("location", parent.getItemAtPosition(pos).toString());*/

        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }

    }
    
    private class DistanceSelectedListener implements OnItemSelectedListener {

        TextView specifyDistance = (TextView) findViewById(R.id.Text_Search_Specify_Distance);

        EditText editDistance = (EditText) findViewById(R.id.Edit_Search_Specify_Distance);


        public void onItemSelected(AdapterView<?> parent,
                        View view, int pos, long id) {

            if (parent.getItemAtPosition(pos).toString().equals("Other")) {
                specifyDistance.setVisibility(View.VISIBLE);
                editDistance.setVisibility(View.VISIBLE);
                //TODO get default units from phone. set in preferences.
                String units = " kilometers.";
                specifyDistance.setText(getString(R.string.Search_Specify_Distance) + units);
            } else {
                specifyDistance.setVisibility(View.GONE);
                editDistance.setVisibility(View.GONE);
            }
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }

    }
}
