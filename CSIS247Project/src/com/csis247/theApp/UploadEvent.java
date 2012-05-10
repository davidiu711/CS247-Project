package com.csis247.theApp;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.csis247.theApp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;


public class UploadEvent extends AsyncTask<Void, Void, Void> {

    ProgressDialog progressDisplay;

    Activity callingActivity;
    
    AlertDialog alertDialog;

    private String event_name;
    private String event_description;
    private String event_address;
    private String event_time;
    private String event_date;

    private EditText eventName;
    private EditText eventDescription;
    private EditText eventAddress;
    private TextView eventTime;
    private TextView eventDate;
    private double lat;
    private double lon;
    
    boolean noAddress = false;

    public UploadEvent(Activity activity, EditText name, EditText description, EditText address, TextView time, TextView date) {
        callingActivity = activity;
        eventName = name;
        eventDescription = description;
        eventAddress = address;
        eventTime = time;
        eventDate = date;

    }


    @Override
    protected void onPreExecute() {
        /*show a dialog message that says "Uploading..." while the
         * events are being downloaded. */
        progressDisplay = new ProgressDialog(callingActivity);
        progressDisplay.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDisplay.setMessage(callingActivity.getResources().getText(R.string.Upload_Event_Dialog));
        progressDisplay.show();

        alertDialog = new AlertDialog.Builder(callingActivity).create();
        alertDialog.setTitle(callingActivity.getText(R.string.Upload_Event_Alert_Dialog_Title));
        alertDialog.setMessage(callingActivity.getText(R.string.Upload_Event_Alert_Dialog_Text)); 
    }


    @Override
    protected Void doInBackground(Void... arg0) {


        try {
            event_name = URLEncoder.encode(eventName.getText().toString(), "UTF-8");
            event_description = URLEncoder.encode(eventDescription.getText().toString(), "UTF-8");
            event_address = URLEncoder.encode(eventAddress.getText().toString(), "UTF-8");
            event_time = eventTime.getText().toString();
            event_date = eventDate.getText().toString();

            SharedPreferences locale = callingActivity.getSharedPreferences("locale", Context.MODE_PRIVATE);
            String country =  locale.getString("country", "PROBLEM");

            /* gets the lat, lon coordinates from an address */
            Geocoder coder = new Geocoder(callingActivity);
            List<Address> address;
            address = coder.getFromLocationName(event_address,5);
            if (address.size() == 0) {
                noAddress = true;
                return null;
            }
            Address location = address.get(0);
            lat = location.getLatitude();
            lon = location.getLongitude();



            /*
             * 
             * So Eric: you have the event_name, event_description, event_address, event_time, event_date, lat, lon, and country that
             * should be uploaded to the server. That's 8 things. they're all strings so everything is ready to go.
             * the country string is important because the date will be in a different order depending on which country the user is from.
             * 
             * 
             * Change the string link string below to incorporate all those things.
             * 
             * 
             * 
             */

            
            String link = "http://i.cs.hku.hk/~stlee/gowhere_create_event.php?event_name="+event_name+"&event_description="+event_description+"&event_address="+event_address+"&";
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

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDisplay.dismiss();
        if (noAddress) {
            alertDialog.show();
        }
    }


}
