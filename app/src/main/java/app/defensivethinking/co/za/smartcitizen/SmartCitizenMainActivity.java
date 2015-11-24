package app.defensivethinking.co.za.smartcitizen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import app.defensivethinking.co.za.smartcitizen.sync.SmartCitizenSyncAdapter;


public class SmartCitizenMainActivity extends AppCompatActivity implements SmartCitizenMainFragment.MyMainActivityInterface,SmartCitizenMainFragment.Callback {

    private final String LOG_TAG = SmartCitizenMainActivity.class.getSimpleName();
    public static String user_email , property_owner;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_citizen_main);

        if (findViewById(R.id.view_property_container) != null ) {

            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.view_property_container, new PropertyDetailActivityFragment())
                        .commit();
            }

        } else {
            mTwoPane = false;
        }
        Bundle arguments = new Bundle();
        arguments.putBoolean(SmartCitizenMainFragment.mTwoPaneKey, mTwoPane);
        SmartCitizenMainFragment fragment = new SmartCitizenMainFragment();
        fragment.setArguments(arguments);
        fragment.setRetainInstance(true);

        getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_main , fragment).commit();

        SmartCitizenSyncAdapter.initializeSyncAdapter(this);
    }

    public void MainActivityData (String u_email , String p_owner) {
        user_email = u_email;
        property_owner = p_owner;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_smart_citizen_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.capture_reading) {
            CaptureReading();
        }
        else if ( id == R.id.view_reading) {
            ViewReading();
        }
        else if ( id == R.id.notifications) {
            Notification();
        }
        else if (id == R.id.add_property) {
            Property();
        }
        else if (id == R.id.logout) {
            deleteUser();
            Intent intent = new Intent(SmartCitizenMainActivity.this, SmartCitizenLoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Notification() {
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

    public void CaptureReading () {
        Intent intent = new Intent(this, CaptureReadingActivity.class);
        intent.putExtra("user_email", user_email);
        intent.putExtra("property_owner", property_owner);
        startActivity(intent);
    }

    public void ViewReading() {
        Intent intent = new Intent(this, ViewReadingActivity.class);
        intent.putExtra("user_email", user_email);
        intent.putExtra("property_owner", property_owner);
        startActivity(intent);
    }

    public void Property() {
        Intent intent = new Intent(this, PropertyActivity.class);
        intent.putExtra("user_email", user_email);
        intent.putExtra("property_owner", property_owner);
        startActivity(intent);
    }

    public void deleteUser () {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user", "");
        editor.commit();

    }

    public void onItemSelected(String property_id) {
         if (mTwoPane) {

                Bundle arguments = new Bundle();
                arguments.putString(PropertyDetailActivity.ACCOUNT_KEY,property_id);

                PropertyDetailActivityFragment fragment = new PropertyDetailActivityFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.view_property_container, fragment)
                        .commit();
         } else {
            Intent intent = new Intent(this, PropertyDetailActivity.class);
            intent.putExtra(PropertyDetailActivity.ACCOUNT_KEY,property_id);
            startActivity(intent);
         }

    }

}
