package app.defensivethinking.co.za.smartcitizen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.utility.utility;


public class CaptureReadingActivity extends ActionBarActivity {

    private static final String LOG_TAG = CaptureReadingActivity.class.getSimpleName();
    private static  EditText water_reading, electricity_reading;


    private static final String[] USER_PROJECTION = {
            SmartCitizenContract.UserEntry.COLUMN_USER_ID,
            SmartCitizenContract.UserEntry.COLUMN_USER_EMAIL,
            SmartCitizenContract.UserEntry.COLUMN_USERNAME,
            SmartCitizenContract.UserEntry.COLUMN_UPDATED
    };

    private static final String[] PROPERTY_PROJECTION = {SmartCitizenContract.PropertyEntry._ID,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_CONTACT_TEL,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PORTION,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_EMAIL,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_BP,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ID,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_SURNAME,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_INITIALS,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PHYSICAL_ADDRESS

    };

    private static final String meter_water_reading = "";
    private static final String meter_electricity_reading = "";
    private static final String account_name = "";
    private static final String surname = "";
    private static final String address = "";
    private static final String contact = "";
    private static final String email = "";

    public static String property_owner = "";

    static int REQUEST_WATER_IMAGE_CAPTURE = 1;
    static int REQUEST_ELECTRICITY_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView water_reading_pic, electricity_reading_pic;
    Spinner acc_name;
    EditText acc_water_reading, acc_electricity_reading, acc_surname, acc_address,
            acc_contact, acc_email , acc_bp , acc_portion, acc_date;
    Cursor property_cursor,user_cursor;
    int[] myPropertyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_reading);

        water_reading_pic = (ImageView) findViewById(R.id.imgWaterProof);
        electricity_reading_pic = (ImageView) findViewById(R.id.imgElectricityProof);

        water_reading  = (EditText) findViewById(R.id.water_reading);
        electricity_reading = (EditText) findViewById(R.id.electricity_reading);

        acc_water_reading = (EditText) findViewById(R.id.water_reading);
        acc_electricity_reading = (EditText) findViewById(R.id.electricity_reading);
        acc_name = (Spinner) findViewById(R.id.account_name);
        acc_surname = (EditText) findViewById(R.id.surname);
        acc_address = (EditText) findViewById(R.id.address);
        acc_contact  = (EditText) findViewById(R.id.contact);
        acc_email = (EditText) findViewById(R.id.email);
        acc_bp  = (EditText) findViewById(R.id.bp);
        acc_portion = (EditText) findViewById(R.id.portion);
        acc_date = (EditText) findViewById(R.id.date);

        Date date = new Date();
        String today = utility.getDbDateString(date);

        acc_date.setText(today);
        Uri properties = SmartCitizenContract.PropertyEntry.CONTENT_URI;


        final String email_address = getUsername();
        Log.i("Email ", email_address);
        String userSelection = "(" + SmartCitizenContract.UserEntry.COLUMN_USER_EMAIL + " = ? )";
        String[] userSelectAgs = new String[] {email_address};

        user_cursor =  getContentResolver().query(SmartCitizenContract.UserEntry.CONTENT_URI , USER_PROJECTION, userSelection, userSelectAgs, null);
        if (user_cursor != null && user_cursor.moveToFirst()) {
            property_owner = user_cursor.getString(0);
            Log.i("User", DatabaseUtils.dumpCursorToString(user_cursor));
            //acc_name.setText();
        }
        String propertySelection = "(" + SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER + " = ? )";
        String[] propertySelectAgs = new String[] {property_owner};

        property_cursor = getContentResolver().query(properties, PROPERTY_PROJECTION, propertySelection, propertySelectAgs, null);

        final List<String> accountNameList = new ArrayList<String>();
        final List<String> accountIdList = new ArrayList<String>();

        if ( property_cursor.moveToFirst()){

            do {
                accountNameList.add(property_cursor.getString(3));
                accountIdList.add(property_cursor.getString(0));
            } while (property_cursor.moveToNext());

        }


        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,accountNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acc_name.setAdapter(adapter);
        acc_name.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        int position = acc_name.getSelectedItemPosition();

                        String  _ID = accountIdList.get(position);
                        if ( property_cursor.moveToFirst()) {
                            do {

                                if ( _ID.equals(property_cursor.getString(0)) ) {

                                    acc_contact.setText(property_cursor.getString(1));
                                    acc_portion.setText(property_cursor.getString(2));
                                    acc_email.setText(property_cursor.getString(5));
                                    acc_bp.setText(property_cursor.getString(6));
                                    acc_surname.setText(property_cursor.getString(8));
                                    acc_address.setText(property_cursor.getString(10));

                                }

                            } while (property_cursor.moveToNext());
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );
        /*

         */

        Button electricity_reading_evidence = (Button) findViewById(R.id.electricity_reading_evidence);
        electricity_reading_evidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REQUEST_WATER_IMAGE_CAPTURE = 0;
                REQUEST_ELECTRICITY_IMAGE_CAPTURE = 1;
                takePictureIntent();
            }
        });

        Button water_reading_evidence = (Button) findViewById(R.id.water_reading_evidence);
        water_reading_evidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REQUEST_ELECTRICITY_IMAGE_CAPTURE = 0;
                REQUEST_WATER_IMAGE_CAPTURE = 1;
                takePictureIntent();
            }
        });

        Button submit_readings = (Button) findViewById(R.id.submit_readings);
        submit_readings.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean cancel = false;
                View focusView = null;

                String account_number = acc_name.getSelectedItem().toString().trim();
                String surname        = acc_surname.getText().toString().trim();
                String address        = acc_address.getText().toString().trim();
                String contact        = acc_contact.getText().toString().trim();
                String email          = acc_email.getText().toString().trim();
                String bp             = acc_bp.getText().toString().trim();
                String portion        = acc_portion.getText().toString().trim();
                String date           = acc_date.getText().toString().trim();
                String water_reading  = acc_water_reading.getText().toString().trim();
                String electricity_reading = acc_electricity_reading.getText().toString().trim();

                if (TextUtils.isEmpty(surname)) {
                    acc_surname.setError(getString(R.string.error_field_required));
                    focusView = acc_surname;
                    cancel = true;
                }

                if (TextUtils.isEmpty(address)) {
                    acc_address.setError(getString(R.string.error_field_required));
                    focusView = acc_address;
                    cancel = true;
                }

                if (TextUtils.isEmpty(contact)) {
                    acc_contact.setError(getString(R.string.error_field_required));
                    focusView = acc_contact;
                    cancel = true;
                }

                if (TextUtils.isEmpty(email)) {
                    acc_email.setError(getString(R.string.error_field_required));
                    focusView = acc_email;
                    cancel = true;
                }

                if (TextUtils.isEmpty(portion)) {
                    acc_portion.setError(getString(R.string.error_field_required));
                    focusView = acc_portion;
                    cancel = true;
                }


                if (TextUtils.isEmpty(bp)) {
                    acc_bp.setError(getString(R.string.error_field_required));
                    focusView = acc_bp;
                    cancel = true;
                }

                if (TextUtils.isEmpty(water_reading)) {
                    acc_water_reading.setError(getString(R.string.error_field_required));
                    focusView =acc_water_reading;
                    cancel = true;
                }

                if (TextUtils.isEmpty(electricity_reading)) {
                    acc_electricity_reading.setError(getString(R.string.error_field_required));
                    focusView =acc_electricity_reading;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    addReading(account_number, surname, address, contact,email,bp, portion,date,water_reading,electricity_reading);
                }

            }
        });

    }

    @Override
    public void onStop() {
        property_cursor.close();
        user_cursor.close();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void takePictureIntent () {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if ( takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if ( requestCode == REQUEST_WATER_IMAGE_CAPTURE && resultCode == RESULT_OK ) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            water_reading_pic.setImageBitmap(imageBitmap);
        }
        else if (requestCode == REQUEST_ELECTRICITY_IMAGE_CAPTURE && resultCode == RESULT_OK ) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            electricity_reading_pic.setImageBitmap(imageBitmap);
        }
    }


    public void addReading(String account_number, String surname, String address, String contact, String email,
                           String bp, String portion, String date, String water_reading, String electricity_reading  ) {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);


        RequestQueue rq = Volley.newRequestQueue(this);

        JSONObject readings = new JSONObject();

        try {
            readings.put("portion", portion);
            readings.put("accountNumber",account_number);
            readings.put("bp", bp);
            readings.put("water", water_reading);
            readings.put("electricity", electricity_reading);
            readings.put("readingDate", date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/readings";

        //Log.i("Reading", readings.toString());

        JsonObjectRequest propertyRequest = new JsonObjectRequest(Request.Method.POST, SMART_CITIZEN_URL,readings, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    Log.i(LOG_TAG, jsonObject.toString());
                } catch (Exception ex ) {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        rq.add(propertyRequest);
    }

    public String getUsername() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString("username", "");
        return username;
    }

}
