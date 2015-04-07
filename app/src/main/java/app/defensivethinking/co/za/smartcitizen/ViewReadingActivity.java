package app.defensivethinking.co.za.smartcitizen;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.UserEntry;
import app.defensivethinking.co.za.smartcitizen.utility.utility;



public class ViewReadingActivity extends ActionBarActivity {

    private static final String LOG_TAG = ViewReadingActivity.class.getSimpleName();

    private static final String[] USER_PROJECTION = new String[] {SmartCitizenContract.UserEntry.COLUMN_USER_ID,
            SmartCitizenContract.UserEntry.COLUMN_USER_EMAIL,
            SmartCitizenContract.UserEntry.COLUMN_USERNAME,
            SmartCitizenContract.UserEntry.COLUMN_UPDATED
    };

    private static final String[] PROPERTY_PROJECTION = new String[] {SmartCitizenContract.PropertyEntry._ID,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER
        };
    Cursor property_cursor, user_cursor;
    Spinner acc_name;
    public static String property_owner = "";
    public static String user_email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reading);

        String email_address = getUsername();
        String userSelection = "(" + UserEntry.COLUMN_USER_EMAIL + " = ? )";
        String[] userSelectAgs = new String[] {email_address};

        acc_name = (Spinner) findViewById(R.id.account_number);

        user_cursor =  getContentResolver().query(SmartCitizenContract.UserEntry.CONTENT_URI , USER_PROJECTION, userSelection, userSelectAgs, null);

        if ( user_cursor != null && user_cursor.moveToFirst() ) {
            property_owner = user_cursor.getString(0);
            user_email  = user_cursor.getString(1);
        }


        getReading();

        Uri property_uri = SmartCitizenContract.PropertyEntry.CONTENT_URI;
        String propertySelection = "(" + SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER + " = ? )";
        String[] propertySelectAgs = new String[] {property_owner};

        property_cursor = getContentResolver().query(property_uri, PROPERTY_PROJECTION, propertySelection, propertySelectAgs, null);

        final List<String> accountNameList = new ArrayList<String>();
        final List<String> accountIdList = new ArrayList<String>();

        if ( property_cursor.moveToFirst()){
            do {
                //Log.i("property cursor", DatabaseUtils.dumpCursorToString(property_cursor));
                accountNameList.add(property_cursor.getString(1));
                accountIdList.add(property_cursor.getString(0));
            } while (property_cursor.moveToNext());
        }

        user_cursor.close();
        //property_cursor.close();

        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,accountNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acc_name.setAdapter(adapter);
        acc_name.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        int position = acc_name.getSelectedItemPosition();
                        String _ID = accountIdList.get(position);
                        String account_id = accountNameList.get(position);
                        getMeterReading(account_id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );
    }

    public String getUsername() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString("username", "");
        return username;
    }

    public void getReading() {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);

        RequestQueue rq = Volley.newRequestQueue(this);
        final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/readings";
        Log.i("url" , SMART_CITIZEN_URL);
        JsonArrayRequest propertyRequest = new JsonArrayRequest( SMART_CITIZEN_URL,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    Vector<ContentValues> cVVector = new Vector<ContentValues>(response.length());
                    for  ( int  i = 0; i < response.length(); i++) {

                        JSONObject meter_reading = (JSONObject) response.get(i);
                        Log.i("Readings", meter_reading.toString());
                        String account_no = meter_reading.getString("account");
                        if ( account_no.equals(null)) {
                            String water_reading = meter_reading.getString("water");
                            String electricity_reading = meter_reading.getString("electricity");
                            String meter_id = meter_reading.getString("_id");
                            String reading_date = meter_reading.getString("updated");

                            ContentValues meterValues = new ContentValues();
                            meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_ID, meter_id);
                            meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_WATER, water_reading);
                            meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_ELECTRICITY, electricity_reading);
                            meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_ACCOUNT_NUMBER,account_no);
                            meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_READING_DATE, reading_date );
                            cVVector.add(meterValues);
                        }

                    }

                    if ( cVVector.size() > 0 ) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        try {
                            getContentResolver().bulkInsert(SmartCitizenContract.MeterReading.CONTENT_URI, cvArray);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getContentResolver().notifyChange(SmartCitizenContract.MeterReading.CONTENT_URI, null);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Volley: " + error.getMessage());
            }

        });

        rq.add(propertyRequest);
    }

    public void getMeterReading(String account_id) {

        Log.i("account id", account_id );
        final String[] METER_PROJECTION = new String[] {

                SmartCitizenContract.MeterReading.COLUMN_METER_ACCOUNT_NUMBER,
                SmartCitizenContract.MeterReading.COLUMN_METER_ELECTRICITY,
                SmartCitizenContract.MeterReading.COLUMN_METER_WATER,
                SmartCitizenContract.MeterReading.COLUMN_METER_READING_DATE
        };
        String meterSelection = "(" + SmartCitizenContract.MeterReading.COLUMN_METER_ACCOUNT_NUMBER + " = ? )";
        String[] meterSelectAgs = new String[] {account_id};

        Uri meter_uri = SmartCitizenContract.MeterReading.CONTENT_URI;

        Cursor meter_cursor = getContentResolver().query(meter_uri,METER_PROJECTION,meterSelection,meterSelectAgs, null );

        if ( meter_cursor.moveToFirst()) {
            do {
                Log.i("meter cursor", DatabaseUtils.dumpCursorToString(meter_cursor));


            } while (meter_cursor.moveToNext());
        }

        meter_cursor.close();

    }
}
