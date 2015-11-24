package app.defensivethinking.co.za.smartcitizen;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import app.defensivethinking.co.za.smartcitizen.adapter.ReadingsAdapter;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.UserEntry;
import app.defensivethinking.co.za.smartcitizen.utility.utility;



public class ViewReadingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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
    ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final int METER_LOADER = 0;
    private static final int METER_ACCOUNT_LOADER = 1;

    private static String account_id = "0";

    private ReadingsAdapter mReadingsAdapter;
    private static final String METER_SELECTED_KEY = "selected_position";

    final String[] METER_PROJECTION = new String[] {
            SmartCitizenContract.MeterReading._ID,
            SmartCitizenContract.MeterReading.COLUMN_METER_ACCOUNT_NUMBER,
            SmartCitizenContract.MeterReading.COLUMN_METER_ELECTRICITY,
            SmartCitizenContract.MeterReading.COLUMN_METER_WATER,
            SmartCitizenContract.MeterReading.COLUMN_METER_READING_DATE
    };

    final List<String> accountNameList = new ArrayList<>();
    final List<String> accountIdList = new ArrayList<>();

    utility _utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reading);
        _utility = new utility(getApplicationContext());
        mListView = (ListView) findViewById(R.id.listview_readings);
        mReadingsAdapter = new ReadingsAdapter(getApplicationContext(),null, 0);

        mListView.setAdapter(mReadingsAdapter);

        String email_address = getUsername();
        String userSelection = "(" + UserEntry.COLUMN_USER_EMAIL + " = ? )";
        String[] userSelectAgs = new String[] {email_address};

        acc_name = (Spinner) findViewById(R.id.account_number);

        user_cursor =  getContentResolver().query(SmartCitizenContract.UserEntry.CONTENT_URI , USER_PROJECTION, userSelection, userSelectAgs, null);

        if ( user_cursor != null && user_cursor.moveToFirst() ) {
            property_owner = user_cursor.getString(0);
            user_email  = user_cursor.getString(1);
            user_cursor.close();
        }



        Uri property_uri = SmartCitizenContract.PropertyEntry.CONTENT_URI;
        String propertySelection = "(" + SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER + " = ? )";
        String[] propertySelectAgs = new String[] {property_owner};

        getReading();

        property_cursor = getContentResolver().query(property_uri, PROPERTY_PROJECTION, propertySelection, propertySelectAgs, null);



        if ( property_cursor!=null && property_cursor.moveToFirst()){
            do {
                accountNameList.add(property_cursor.getString(1));
                accountIdList.add(property_cursor.getString(0));
            } while (property_cursor.moveToNext());
        }
        else {
            TextView no_properties = (TextView) findViewById(R.id.no_properties);
            no_properties.setText("You have no meter readings");
            View view_reading_layout = findViewById(R.id.view_reading_layout);
            view_reading_layout.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,accountNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acc_name.setAdapter(adapter);
        acc_name.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        int position = acc_name.getSelectedItemPosition();
                        String account_id = accountNameList.get(position);

                        getMeterReading(account_id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                }
        );

        if (savedInstanceState != null && savedInstanceState.containsKey(METER_SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(METER_SELECTED_KEY);
        }

        getSupportLoaderManager().initLoader(METER_LOADER, null, this);
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

    public void getReading() {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);

        RequestQueue rq = Volley.newRequestQueue(this);
        final String base_url = utility.base_url; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/readings";

        JsonArrayRequest propertyRequest = new JsonArrayRequest( SMART_CITIZEN_URL,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {

                    Vector<ContentValues> cVVector = new Vector<>(response.length());
                    for  ( int  i = 0; i < response.length(); i++) {

                        JSONObject meter_reading = (JSONObject) response.get(i);

                        if( meter_reading.has("account") && meter_reading.has("electricity")) {
                            if ( accountNameList.contains(meter_reading.getString("account")) ) {
                                String account_no = meter_reading.getString("account");
                                String water_reading = meter_reading.getString("water");
                                String electricity_reading = meter_reading.getString("electricity");
                                String meter_id = meter_reading.getString("_id");
                                String reading_date = meter_reading.getString("updated");

                                ContentValues meterValues = new ContentValues();
                                meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_ID, meter_id);
                                meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_WATER, water_reading);
                                meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_ELECTRICITY, electricity_reading);
                                meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_ACCOUNT_NUMBER, account_no);
                                meterValues.put(SmartCitizenContract.MeterReading.COLUMN_METER_READING_DATE, reading_date);
                                cVVector.add(meterValues);

                            }


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

                Log.i("Error", error_msg);
            }

        });

        rq.add(propertyRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        property_cursor.close();
    }

    public void getMeterReading(String acc_id) {
        account_id = acc_id;
        getSupportLoaderManager().restartLoader(METER_ACCOUNT_LOADER, null, this); //initLoader(METER_ACCOUNT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri meter_uri = SmartCitizenContract.MeterReading.CONTENT_URI;
        String sortOrder = SmartCitizenContract.MeterReading.COLUMN_METER_READING_DATE + " DESC";
        if (id != 0) {
            String meterSelection = "(" + SmartCitizenContract.MeterReading.COLUMN_METER_ACCOUNT_NUMBER + " = ? )";
            String[] meterSelectAgs = new String[] {""+account_id};
            return new CursorLoader(
                    this,
                    meter_uri,
                    METER_PROJECTION,
                    meterSelection,
                    meterSelectAgs,
                    sortOrder );

        } else {
            return new CursorLoader(
                    this,
                    meter_uri,
                    METER_PROJECTION,
                    null,
                    null,
                    sortOrder
            );
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mReadingsAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReadingsAdapter.swapCursor(null);
    }
}
