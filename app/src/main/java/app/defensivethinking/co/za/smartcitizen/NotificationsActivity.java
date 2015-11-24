package app.defensivethinking.co.za.smartcitizen;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import app.defensivethinking.co.za.smartcitizen.utility.utility;


public class NotificationsActivity extends AppCompatActivity {


    String owner, email;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        context = getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if ( extras != null) {
            email = extras.getString("user_email");
            owner = extras.getString("property_owner");
        }

        getNotification();
    }

    public void getNotification () {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);

        RequestQueue rq = Volley.newRequestQueue(context);
        final String base_url = utility.base_url; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/notifications/me";
        JSONObject notification = new JSONObject();
        try {
            notification.put("me", owner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest propertyRequest = new JsonObjectRequest(Request.Method.GET, SMART_CITIZEN_URL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("Notifications", jsonObject.toString());
            }
        }
                , new Response.ErrorListener() {
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
}
