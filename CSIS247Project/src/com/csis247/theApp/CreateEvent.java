package com.csis247.theApp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CreateEvent extends Activity implements OnClickListener{

    /** Event name input */
    private EditText eventName;

    /** Event Description input */
    private EditText eventDescription;

    /** Event Address input */
    private EditText eventAddress;

    /** Event time input */
    private EditText eventTime;

    /** submit information button */
    private Button submit;

    /** access pictures button */
    private Button picture;

    /** the file path of the selected image */
    private String selectedImagePath;

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

        submit = (Button) findViewById(R.id.button_submit_event_info);
        submit.setOnClickListener(this);

        picture = (Button) findViewById(R.id.button_picture_event_info);
        picture.setOnClickListener(this);

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
        case R.id.button_picture_event_info :
            
            //open a menu to pick a picture.
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), 1);
            break;
        case R.id.button_submit_event_info :

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
                view.addView(img, 8);
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
