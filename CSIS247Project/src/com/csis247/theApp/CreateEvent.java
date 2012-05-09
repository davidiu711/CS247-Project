package com.csis247.theApp;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Layout;
import android.text.format.DateFormat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreateEvent extends Activity implements OnClickListener{

    private Context context;

    /** Event name input */
    private EditText eventName;

    /** Event Description input */
    private EditText eventDescription;

    /** Event Address input */
    private EditText eventAddress;

    /** submit information button */
    private Button submit;

    /** access pictures button */
    private Button picture;

    /** the file path of the selected image */
    private String selectedImagePath;

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
    
    private String event_name;
    private String event_description;
    private String event_address;
    private String event_datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.createevent);

        context = getApplicationContext();

        intitalizeWidgets();
    }

    /** find and set widgets in this view */
    private void intitalizeWidgets() {

        eventName = (EditText) findViewById(R.id.Edit_Event_Name);
        eventDescription = (EditText) findViewById(R.id.Edit_Create_Event_Description);
        eventAddress = (EditText) findViewById(R.id.Edit_Create_Event_Address);

        submit = (Button) findViewById(R.id.Button_submit_event_info);
        submit.setOnClickListener(this);

        picture = (Button) findViewById(R.id.Button_Create_Event_Picture);
        picture.setOnClickListener(this);

        /* button and textview for selecting and displaying event time */
        mTimeDisplay = (TextView) findViewById(R.id.Text_Create_Chosen_Time);
        mPickTime = (Button) findViewById(R.id.Button_Create_Event_Time);
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });


        /* button and textview for selecting and displaying event date */
        mDateDisplay = (TextView) findViewById(R.id.Text_Create_Chosen_Date);
        mPickDate = (Button) findViewById(R.id.Button_Create_Event_Date);
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(1);
            }
        });

        /* setting the current time and date as the initially displayed time and date */
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
       Calendar cal  = new GregorianCalendar();
        cal.set(mYear, mMonth, mDay, mHour, mMinute);
        
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        
        mTimeDisplay.setText(timeFormat.format(cal.getTime()));

        mDateDisplay.setText(dateFormat.format(cal.getTime()));
    }


    @Override
    protected void onPause() {
        super.onPause();
        //TODO store typed information from edit text and store picture (if any).
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO repopulate edit texts with stored text and re-display picture (if any).
    }

    @Override
    public void onClick(View v) {


        switch(v.getId()) {
        case R.id.Button_Create_Event_Picture :

            //open a menu to pick a picture.
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), 1);
            break;
        case R.id.Button_submit_event_info :

            /* TODO send sumbitted data to server. Use getData function in Utils class.
             *  Use selectedImagePath to access the picture file for upload. */
        	try {
        		String link, temp;
            	event_name = URLEncoder.encode(eventName.getText().toString(), "UTF-8");
        		event_description = URLEncoder.encode(eventDescription.getText().toString(), "UTF-8");
        		event_address = URLEncoder.encode(eventAddress.getText().toString(), "UTF-8");
        		temp = mYear+"-"+mMonth+"-"+mDay+" "+mHour+":"+mMinute+":00";
        		event_datetime = URLEncoder.encode(temp, "UTF-8");
        		System.out.println("datetime: "+event_datetime);
        		link = "http://i.cs.hku.hk/~stlee/gowhere_create_event.php?event_name="+event_name+"&event_description="+event_description+"&event_address="+event_address+"&event_datetime="+event_datetime;
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
            break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                //get picture URI
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                ImageView img = new ImageView(getApplicationContext());

                //display selected image on screen
                img.setImageURI(selectedImageUri);
                img.setAdjustViewBounds(true);
                img.setMaxHeight(100);
                img.setMaxWidth(100);
                LinearLayout view = (LinearLayout) findViewById(R.id.LinearLayout_Create_Event);
                view.addView(img, 12);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
