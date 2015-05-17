package app.defensivethinking.co.za.smartcitizen;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class PropertyDetailActivity extends ActionBarActivity {


    public static final String ACCOUNT_KEY = "account_id";
    public static final String ACCOUNT_NO = "account_no";

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_property_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
