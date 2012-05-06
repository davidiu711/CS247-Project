package com.csis247.theApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import android.text.Layout;
import android.text.format.DateFormat;

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
    
    private TextView mTimeDisplay;
    private Button mPickTime;
    private int mHour;
    private int mMinute;

    private TextView mDateDisplay;
    private Button mPickDate;
    private Integer mYear;
    private Integer mMonth;
    private Integer mDay;

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
        
        mTimeDisplay = (TextView) findViewById(R.id.Text_Create_Chosen_Time);
        mPickTime = (Button) findViewById(R.id.Button_Create_Event_Time);

        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });
        
        
        // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.Text_Create_Chosen_Date);
        mPickDate = (Button) findViewById(R.id.Button_Create_Event_Date);

        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(1);
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


    @Override
    protected void onPause() {
        super.onPause();
        //TODO store typed information from edit text and store picture (if any).
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO repopulate edit tests with store texts and re-display picture (if any).
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

}
