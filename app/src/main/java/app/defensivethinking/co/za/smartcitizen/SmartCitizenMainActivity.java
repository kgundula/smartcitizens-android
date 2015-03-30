package app.defensivethinking.co.za.smartcitizen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.PropertyEntry;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.UserEntry;


public class SmartCitizenMainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayAdapter<String> mPropertyAdapter;
    Context context;
    Cursor property_cursor, user_cursor;


    private List<PropertyAdapter.Property> propertyItemList = new ArrayList<PropertyAdapter.Property>();

    private static final String[] PROJECTION = new String[]{PropertyEntry._ID,
                PropertyEntry.COLUMN_PROPERTY_CONTACT_TEL,
                PropertyEntry.COLUMN_PROPERTY_PHYSICAL_ADDRESS,
                PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER};

    private static final String[] USER_PROJECTION = new String[] {UserEntry.COLUMN_USER_ID,
            UserEntry.COLUMN_USER_EMAIL,
            UserEntry.COLUMN_USERNAME,
            UserEntry.COLUMN_UPDATED
    };

    private static final int PROPERTY_LOADER = 0;
    public static String property_owner = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_citizen_main);
        context = getApplicationContext();

       // getLoaderManager().initLoader(PROPERTY_LOADER, null, this);

        TextView welcome_email_text = (TextView) findViewById(R.id.welcome_email_text);
        user_cursor = getContentResolver().query(UserEntry.CONTENT_URI,USER_PROJECTION, null, null, null);
        //String property_owner = "";
        if ( user_cursor != null && user_cursor.moveToFirst() ) {
            property_owner = user_cursor.getString(0);
            welcome_email_text.setText(user_cursor.getString(1));
            Log.i("Cursor", DatabaseUtils.dumpCursorToString(user_cursor));
        }

        ListView listView = (ListView) findViewById(R.id.listview_properties);
        listView.setAdapter(mPropertyAdapter);

        ImageView imgCaptureReading = (ImageView) findViewById(R.id.imgCaptureReading);
        imgCaptureReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureReading();
            }
        });

        TextView txtCaptureReading = (TextView) findViewById(R.id.txtCaptureReading);
        txtCaptureReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureReading();
            }
        });


        ImageView imgViewReading = (ImageView) findViewById(R.id.imgViewReading);
        imgViewReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewReading();
            }
        });

        TextView txtViewReading = (TextView) findViewById(R.id.txtViewReading);
        txtViewReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewReading();
            }
        });

        ImageView imgNotification = (ImageView) findViewById(R.id.imgNotification);
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification();
            }
        });

        TextView txtNotification = (TextView) findViewById(R.id.txtNotification);
        txtNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification();
            }
        });


    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER + " ASC";

        Uri uri = PropertyEntry.CONTENT_URI;

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                this.getActivity(),
                uri,
                PROJECTION,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("loader", DatabaseUtils.dumpCursorToString(data) );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onStop() {
       // property_cursor.close();
        user_cursor.close();
        super.onStop();
    }

    public void Notification() {
        Intent intent = new Intent(SmartCitizenMainActivity.this, NotificationsActivity.class);
        startActivity(intent);
    }

    public void CaptureReading () {
        Intent intent = new Intent(SmartCitizenMainActivity.this, CaptureReadingActivity.class);
        startActivity(intent);

    }

    public void ViewReading() {
        Intent intent = new Intent(SmartCitizenMainActivity.this, ViewReadingActivity.class);
        startActivity(intent);
    }

    public void Property() {
        Intent intent = new Intent(SmartCitizenMainActivity.this, PropertyActivity.class);
        intent.putExtra("user_id", property_owner);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smart_citizen_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
            deleteUsername();
            deletePassword();
            Intent intent = new Intent(SmartCitizenMainActivity.this, SmartCitizenLoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteUsername () {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username","");
        editor.commit();
    }

    public void deletePassword () {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("password","");
        editor.commit();
    }



}
