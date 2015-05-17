package app.defensivethinking.co.za.smartcitizen;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class PropertyDetailActivity extends ActionBarActivity {

    public static final String ACCOUNT_KEY = "account_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        if (savedInstanceState == null) {

            String account_id = getIntent().getStringExtra(ACCOUNT_KEY);
            Bundle arguments = new Bundle();
            arguments.putString( PropertyDetailActivity.ACCOUNT_KEY , account_id);

            PropertyDetailActivityFragment fragment = new PropertyDetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.view_property_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
