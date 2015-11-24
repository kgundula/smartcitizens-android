package app.defensivethinking.co.za.smartcitizen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.utility.utility;


public class CaptureReadingActivity extends AppCompatActivity {

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

    final String[] METER_READING_PROJECTION = new String[] {
            SmartCitizenContract.MeterReading._ID,
            SmartCitizenContract.MeterReading.COLUMN_METER_ELECTRICITY,
            SmartCitizenContract.MeterReading.COLUMN_METER_WATER,
            SmartCitizenContract.MeterReading.COLUMN_METER_READING_DATE
    };

    private static String meter_water_reading = "";
    private static String meter_electricity_reading = "";
    private static String account_name = "";
    private static String surname = "";
    private static String address = "";
    private static String contact = "";
    private static String email = "";
    private static String bp = "";
    private static String portion = "";
    private static String my_acc_date = "";

    private static String previous_water_reading = "";
    private static String previous_electricity_reading = "";

    public static String property_owner = "";

    static int REQUEST_WATER_IMAGE_CAPTURE = 1;
    static int REQUEST_ELECTRICITY_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView water_reading_pic, electricity_reading_pic;
    Spinner acc_name;
    TextView error_message;
    EditText acc_water_reading, acc_electricity_reading, acc_surname, acc_address,
            acc_contact, acc_email , acc_bp , acc_portion, acc_date;
    Cursor property_cursor,user_cursor;
    static ProgressBar progressBar;
    Context context;
    utility _utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_reading);

        context = getApplicationContext();
        _utility = new utility(context);

        water_reading_pic = (ImageView) findViewById(R.id.imgWaterProof);
        electricity_reading_pic = (ImageView) findViewById(R.id.imgElectricityProof);

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
        error_message = (TextView) findViewById(R.id.error_message);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final Date date = new Date();
        String today = utility.getDbDateString(date);

        acc_date.setText(today);
        Uri properties = SmartCitizenContract.PropertyEntry.CONTENT_URI;

        final String email_address = getUsername();

        String userSelection = "(" + SmartCitizenContract.UserEntry.COLUMN_USER_EMAIL + " = ? )";
        String[] userSelectAgs = new String[] {email_address};

        user_cursor =  getContentResolver().query(SmartCitizenContract.UserEntry.CONTENT_URI , USER_PROJECTION, userSelection, userSelectAgs, null);
        if (user_cursor != null && user_cursor.moveToFirst()) {
            property_owner = user_cursor.getString(0);
        }
        String propertySelection = "(" + SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER + " = ? )";
        String[] propertySelectAgs = new String[] {property_owner};

        property_cursor = getContentResolver().query(properties, PROPERTY_PROJECTION, propertySelection, propertySelectAgs, null);

        final List<String> accountNameList = new ArrayList<>();
        final List<String> accountIdList = new ArrayList<>();

        TextView no_properties = (TextView) findViewById(R.id.no_properties);

        if ( property_cursor.moveToFirst()){

            do {
                accountNameList.add(property_cursor.getString(3));
                accountIdList.add(property_cursor.getString(0));
            } while (property_cursor.moveToNext());

            no_properties.setVisibility(View.GONE);
        }
        else
        {
            no_properties.setText("Your have no properties yet, Please add a property");
            View capture_reading_layout = findViewById(R.id.capture_reading_layout);
            capture_reading_layout.setVisibility(View.INVISIBLE);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acc_name.setAdapter(adapter);
        acc_name.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        int position = acc_name.getSelectedItemPosition();

                        String _ID = accountIdList.get(position);
                        previous_electricity_reading = "0";
                        previous_water_reading = "0";
                        if (property_cursor.moveToFirst()) {
                            do {

                                if (_ID.equals(property_cursor.getString(0))) {

                                    Uri meter_uri = SmartCitizenContract.MeterReading.CONTENT_URI;
                                    String sortOrder = SmartCitizenContract.MeterReading.COLUMN_METER_READING_DATE + " DESC";

                                    String meterSelection = "(" + SmartCitizenContract.MeterReading.COLUMN_METER_ACCOUNT_NUMBER + " = ? )";
                                    String[] meterSelectAgs = new String[]{property_cursor.getString(3)};

                                    Cursor meter_reading = getContentResolver().query(meter_uri, METER_READING_PROJECTION, meterSelection, meterSelectAgs, sortOrder);
                                    if (meter_reading != null && meter_reading.moveToFirst()) {
                                        previous_electricity_reading = meter_reading.getString(1);
                                        previous_water_reading = meter_reading.getString(2);

                                        meter_reading.close();
                                    } else {
                                        previous_electricity_reading = "0";
                                        previous_water_reading = "0";
                                    }

                                    if (Integer.parseInt(previous_electricity_reading) == 0) {
                                        acc_electricity_reading.setText("");
                                    } else {
                                        acc_electricity_reading.setText(previous_electricity_reading);
                                    }


                                    if (Integer.parseInt(previous_electricity_reading) == 0) {
                                        acc_water_reading.setText("");
                                    } else {
                                        acc_water_reading.setText(previous_water_reading);
                                    }


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
        submit_readings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean cancel = false;
                View focusView = null;

                account_name = acc_name.getSelectedItem().toString().trim();
                surname = acc_surname.getText().toString().trim();
                address = acc_address.getText().toString().trim();
                contact = acc_contact.getText().toString().trim();
                email = acc_email.getText().toString().trim();
                bp = acc_bp.getText().toString().trim();
                portion = acc_portion.getText().toString().trim();
                my_acc_date = acc_date.getText().toString().trim();
                meter_water_reading = acc_water_reading.getText().toString().trim();
                meter_electricity_reading = acc_electricity_reading.getText().toString().trim();

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

                if (TextUtils.isEmpty(meter_water_reading)) {
                    acc_water_reading.setError(getString(R.string.error_field_required));
                    focusView = acc_water_reading;
                    cancel = true;
                }

                if (TextUtils.isEmpty(meter_electricity_reading)) {
                    acc_electricity_reading.setError(getString(R.string.error_field_required));
                    focusView = acc_electricity_reading;
                    cancel = true;
                }

                if (meter_electricity_reading != null && !TextUtils.isEmpty(meter_electricity_reading)) {
                    if (Integer.parseInt(previous_electricity_reading) >= Integer.parseInt(meter_electricity_reading)) {
                        acc_electricity_reading.setError(getString(R.string.error_reading_less));
                        focusView = acc_electricity_reading;
                        cancel = true;
                    }
                }

                if (meter_water_reading != null && !TextUtils.isEmpty(meter_water_reading)) {

                    if (Integer.parseInt(previous_water_reading) >= Integer.parseInt(meter_water_reading)) {
                        acc_water_reading.setError(getString(R.string.error_reading_less));
                        focusView = acc_water_reading;
                        cancel = true;
                    }
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    if (!utility.isDeviceConnectedToInternet()) {
                        updateErrorMessage(getString(R.string.no_internet));
                    } else {
                        addReading(account_name, surname, address, contact, email, bp, portion, my_acc_date, meter_water_reading, meter_electricity_reading);

                    }
                }

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        property_cursor.close();
        user_cursor.close();
        super.onDestroy();
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
            readings.put("username", getUsername());

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String base_url = utility.base_url; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/readings";

        JsonObjectRequest propertyRequest = new JsonObjectRequest(Request.Method.POST, SMART_CITIZEN_URL,readings, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                Log.i( LOG_TAG , jsonObject.toString() );

                Toast.makeText(context, getResources().getString(R.string.reading_captured), Toast.LENGTH_LONG).show();
                updateErrorMessage(getResources().getString(R.string.reading_captured));
                Intent mainActivity = new Intent( CaptureReadingActivity.this, SmartCitizenMainActivity.class );
                startActivity(mainActivity);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String error_msg = "";
                if( error instanceof NetworkError) {
                    error_msg = "Network Error";
                } else if( error instanceof ServerError) {
                    error_msg = error.getMessage();
                } else if( error instanceof AuthFailureError) {
                    error_msg = error.getMessage();
                } else if( error instanceof ParseError) {
                    error_msg = error.getMessage();
                } else if( error instanceof TimeoutError) {
                    error_msg = error.getMessage();
                }

                Toast.makeText(context, error_msg, Toast.LENGTH_LONG).show();

            }
        });

        rq.add(propertyRequest);
    }

    public void updateErrorMessage(String text) {
        TextView error_message = (TextView) findViewById(R.id.error_message);
        error_message.setText(text);
        error_message.setTextColor(ContextCompat.getColor(context, R.color.smart_citizen_text_color));
        error_message.setBackgroundColor(ContextCompat.getColor(context,R.color.red_500));
        error_message.setVisibility(View.VISIBLE);
        error_message.invalidate();
    }

    public String getUsername() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = settings.getString("user", "");
        String username = "";
        try {
            JSONObject userObject = new JSONObject(user);
            username = userObject.getString("username");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return username;
    }

}
