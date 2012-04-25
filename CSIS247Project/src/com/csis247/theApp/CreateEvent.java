package com.csis247.theApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateEvent extends Activity implements OnClickListener{

    /** Event name input */
    EditText eventName;
    
    /** Event Description input */
    EditText eventDescription;
    
    /** Event Address input */
    EditText eventAddress;
    
    /** Event time input */
    EditText eventTime;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.createevent);
        
        intitalizeWidgets();
    }
    
    /** find and set widgets in this view */
    private void intitalizeWidgets() {
        
        eventName = (EditText) findViewById(R.id.Edit_Event_Name);
        eventDescription = (EditText) findViewById(R.id.Edit_Event_Description);
        eventAddress = (EditText) findViewById(R.id.Edit_Event_Address);
        eventTime = (EditText) findViewById(R.id.Edit_Event_Time);
        
        Button submit = (Button) findViewById(R.id.button_submit_event_info);
        submit.setOnClickListener(this);
        
    }

    @Override
    public void onClick(View v) {
        // TODO send sumbitted data to server. Use getData function in Utils class
        
    }
    
}
