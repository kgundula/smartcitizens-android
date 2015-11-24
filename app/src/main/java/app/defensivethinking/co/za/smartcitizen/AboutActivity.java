package app.defensivethinking.co.za.smartcitizen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        String version = "App Version : " + getAppVersion();
        TextView app_version = (TextView) findViewById(R.id.app_version);
        app_version.setText(version);

    }

    public String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

}
