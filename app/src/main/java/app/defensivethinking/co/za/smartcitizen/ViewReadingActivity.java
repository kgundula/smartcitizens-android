package app.defensivethinking.co.za.smartcitizen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class ViewReadingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reading);
    }


    public void getReading() {

        RequestQueue rq = Volley.newRequestQueue(this);
        final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/readings";

        //accountNumber

    }

}
