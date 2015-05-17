package app.defensivethinking.co.za.smartcitizen;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.utility.utility;


public class PropertyActivity extends ActionBarActivity {

   private static final String LOG_TAG = PropertyActivity.class.getSimpleName();

   private static EditText property_account, property_address, property_bp, property_contact,
            property_initials, property_surname, property_portion;
    String owner, email;
    static TextView error_message;
    static ProgressBar progressBar;
    Context context;
    utility _utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        context = getApplicationContext();
        _utility = new utility(context);
        Bundle extras = getIntent().getExtras();
        if ( extras != null) {
            email = extras.getString("user_email");
            owner = extras.getString("property_owner");
        }

        error_message = (TextView) findViewById(R.id.error_message);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        property_account = (EditText) findViewById(R.id.property_account_no);
        property_address = (EditText) findViewById(R.id.property_address);
        property_bp      = (EditText) findViewById(R.id.property_bp);
        property_contact = (EditText) findViewById(R.id.property_contact);
        property_initials= (EditText) findViewById(R.id.property_initials);
        property_surname = (EditText) findViewById(R.id.property_surname);
        property_portion = (EditText) findViewById(R.id.property_portion);

        Button submit_property = (Button) findViewById(R.id.submit_property);
        submit_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String portion = property_portion.getText().toString().trim();
                String bp      = property_bp.getText().toString().trim();
                String initials = property_initials.getText().toString().trim();
                String surname = property_surname.getText().toString().trim();
                String contact = property_contact.getText().toString().trim();
                String address = property_address.getText().toString().trim();
                String account = property_account.getText().toString().trim();

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(portion) ) {
                    property_portion.setError(getString(R.string.error_field_required));
                    focusView = property_portion;
                    cancel = true;
                }
                if (TextUtils.isEmpty(bp) ) {
                    property_bp.setError(getString(R.string.error_field_required));
                    focusView = property_bp;
                    cancel = true;
                }
                if (TextUtils.isEmpty(initials) ) {
                    property_initials.setError(getString(R.string.error_field_required));
                    focusView = property_initials;
                    cancel = true;
                }
                if (TextUtils.isEmpty(surname) ) {
                    property_surname.setError(getString(R.string.error_field_required));
                    focusView = property_surname;
                    cancel = true;
                }
                if (TextUtils.isEmpty(contact) ) {
                    property_contact.setError(getString(R.string.error_field_required));
                    focusView = property_contact;
                    cancel = true;
                }
                if (TextUtils.isEmpty(address) ) {
                    property_address.setError(getString(R.string.error_field_required));
                    focusView = property_address;
                    cancel = true;
                }
                if (TextUtils.isEmpty(account) ) {
                    property_account.setError(getString(R.string.error_field_required));
                    focusView = property_account;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {

                    TextView error_message = (TextView) findViewById(R.id.error_message);
                    if (!utility.isDeviceConnectedToInternet()) {
                        error_message.setText("Internet Connection is Required, please connect internet");
                        error_message.setTextColor(getResources().getColor(R.color.smart_citizen_text_color));
                        error_message.setBackgroundColor(getResources().getColor(R.color.red_500));
                        error_message.setVisibility(View.VISIBLE);
                        error_message.invalidate();

                    }
                    else {
                        error_message.setVisibility(View.GONE);
                        error_message.invalidate();
                        addProperty(portion, bp, initials, surname, contact, address, account);
                    }
                }
            }
        });
    }

    public void addProperty(String portion, String bp, String initials, String surname, String contact, String address, String account) {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);

        RequestQueue rq = Volley.newRequestQueue(this);

        JSONObject property = new JSONObject();
        try {
            property.put("portion", portion);
            property.put("accountnumber",account);
            property.put("bp", bp);
            property.put("contacttel", contact);
            property.put("email", email);
            property.put("initials", initials);
            property.put("surname", surname);
            property.put("physicaladdress",address);
            property.put("owner", owner);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/properties";
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest propertyRequest = new JsonObjectRequest(Request.Method.POST, SMART_CITIZEN_URL, property , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    Log.i(LOG_TAG, jsonObject.toString());
                    Boolean success = jsonObject.getBoolean("success");
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.invalidate();
                    String message = "";
                    if ( success ) {
                        message = "Property Added Successfully";
                        JSONObject myProperty = jsonObject.getJSONObject("property");

                        String _id = myProperty.getString("_id");
                        String contact_tel = myProperty.getString("contacttel");
                        String bp          = myProperty.getString("bp");
                        String physical_address = myProperty.getString("physicaladdress");
                        String property_updated = myProperty.getString("updated");
                        String initials = myProperty.getString("initials");
                        String property_email   = myProperty.getString("email");
                        String owner = myProperty.getString("owner");
                        String surname = myProperty.getString("surname");
                        String account_number = myProperty.getString("accountnumber");
                        String portion = myProperty.getString("portion");

                        ContentValues propertyValues = new ContentValues();
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ID, _id);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER, account_number);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_BP, bp);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_CONTACT_TEL,contact_tel);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_EMAIL, property_email);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PORTION, portion);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_SURNAME, surname);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_INITIALS, initials);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER, owner);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_UPDATED, property_updated);
                        propertyValues.put(SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PHYSICAL_ADDRESS,physical_address);

                        try {
                            getContentResolver().insert(SmartCitizenContract.PropertyEntry.CONTENT_URI, propertyValues);
                            getContentResolver().notifyChange(SmartCitizenContract.PropertyEntry.CONTENT_URI, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    else {
                        message = jsonObject.getString("error");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    error_message.setText(message);
                    error_message.setVisibility(View.VISIBLE);
                    error_message.invalidate();

                } catch (Exception ex ) {
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
                } else if( error instanceof NoConnectionError) {
                    error_msg = error.getMessage();
                } else if( error instanceof TimeoutError) {
                    error_msg = error.getMessage();
                }

                error_message.setText(error_msg);
                error_message.setVisibility(View.VISIBLE);
                error_message.invalidate();
            }
        });

        rq.add(propertyRequest);

    }



}
