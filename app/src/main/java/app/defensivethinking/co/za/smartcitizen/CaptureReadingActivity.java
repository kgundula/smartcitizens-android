package app.defensivethinking.co.za.smartcitizen;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Date;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.utility.utility;


public class CaptureReadingActivity extends ActionBarActivity {

    private static final String LOG_TAG = CaptureReadingActivity.class.getSimpleName();
    private static  EditText water_reading, electricity_reading;


    private static final String[] USER_PROJECTION = {
            SmartCitizenContract.UserEntry.COLUMN_USER_ID,
            SmartCitizenContract.UserEntry.COLUMN_USER_EMAIL,
            SmartCitizenContract.UserEntry.COLUMN_USERNAME,
            SmartCitizenContract.UserEntry.COLUMN_UPDATED
    };

    private static final String[] PROPERTY_PROJECTION = {SmartCitizenContract.PropertyEntry._ID,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_CONTACT_TEL,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PORTION,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_OWNER,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_EMAIL,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_BP,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_ID,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_SURNAME,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_INITIALS,
            SmartCitizenContract.PropertyEntry.COLUMN_PROPERTY_PHYSICAL_ADDRESS

    };

    private static final String meter_water_reading = "";
    private static final String meter_electricity_reading = "";
    private static final String account_name = "";
    private static final String surname = "";
    private static final String address = "";
    private static final String contact = "";
    private static final String email = "";


    static int REQUEST_WATER_IMAGE_CAPTURE = 1;
    static int REQUEST_ELECTRICITY_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView water_reading_pic, electricity_reading_pic;
    EditText acc_water_reading, acc_electricity_reading,acc_name, acc_surname, acc_address,
            acc_contact, acc_email , acc_bp , acc_portion, acc_date;
    Cursor property_cursor,user_cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_reading);

        water_reading_pic = (ImageView) findViewById(R.id.imgWaterProof);
        electricity_reading_pic = (ImageView) findViewById(R.id.imgElectricityProof);

        water_reading  = (EditText) findViewById(R.id.water_reading);
        electricity_reading = (EditText) findViewById(R.id.electricity_reading);

        acc_water_reading = (EditText) findViewById(R.id.water_reading);
        acc_electricity_reading = (EditText) findViewById(R.id.electricity_reading);
        acc_name = (EditText) findViewById(R.id.account_name);
        acc_surname = (EditText) findViewById(R.id.surname);
        acc_address = (EditText) findViewById(R.id.address);
        acc_contact  = (EditText) findViewById(R.id.contact);
        acc_email = (EditText) findViewById(R.id.email);
        acc_bp  = (EditText) findViewById(R.id.bp);
        acc_portion = (EditText) findViewById(R.id.portion);
        acc_date = (EditText) findViewById(R.id.date);

        Date date = new Date();
        String today = utility.getDbDateString(date);

        acc_date.setText(today);
        Uri properties = SmartCitizenContract.PropertyEntry.CONTENT_URI;
        Log.i("property uri", properties.toString());

        property_cursor = getContentResolver().query(properties, PROPERTY_PROJECTION, null, null, null);
        user_cursor = getContentResolver().query(SmartCitizenContract.UserEntry.CONTENT_URI, USER_PROJECTION, null, null, null );

        if ( property_cursor.moveToFirst()){

            do {
                Log.i("Property Cursor", DatabaseUtils.dumpCursorToString(property_cursor));
                acc_contact.setText(property_cursor.getString(1));
                acc_portion.setText(property_cursor.getString(2));
                acc_name.setText(property_cursor.getString(3));
                acc_email.setText(property_cursor.getString(5));
                acc_bp.setText(property_cursor.getString(6));
                acc_surname.setText(property_cursor.getString(8));
                acc_address.setText(property_cursor.getString(10));
            } while (property_cursor.moveToNext());

        }

        if (user_cursor != null && user_cursor.moveToFirst()) {
            Log.i("User", DatabaseUtils.dumpCursorToString(user_cursor));
            //acc_name.setText();
        }
        Button electricity_reading_evidence = (Button) findViewById(R.id.electricity_reading_evidence);
        electricity_reading_evidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REQUEST_WATER_IMAGE_CAPTURE = 0;
                REQUEST_ELECTRICITY_IMAGE_CAPTURE = 1;
                takePictureIntent();
            }
        });

        Button water_reading_evidence = (Button) findViewById(R.id.water_reading_evidence);
        water_reading_evidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REQUEST_ELECTRICITY_IMAGE_CAPTURE = 0;
                REQUEST_WATER_IMAGE_CAPTURE = 1;
                takePictureIntent();
            }
        });

        Button submit_readings = (Button) findViewById(R.id.submit_readings);
        submit_readings.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String account_number = acc_name.getText().toString().trim();
                String surname        = acc_surname.getText().toString().trim();
                String address        = acc_address.getText().toString().trim();
                String contact        = acc_contact.getText().toString().trim();
                String email          = acc_email.getText().toString().trim();
                String bp             = acc_bp.getText().toString().trim();
                String portion        = acc_portion.getText().toString().trim();
                String date           = acc_date.getText().toString().trim();

            }
        });

    }

    @Override
    public void onStop() {
        property_cursor.close();
        user_cursor.close();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_capture_reading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void takePictureIntent () {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if ( takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if ( requestCode == REQUEST_WATER_IMAGE_CAPTURE && resultCode == RESULT_OK ) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            water_reading_pic.setImageBitmap(imageBitmap);
        }
        else if (requestCode == REQUEST_ELECTRICITY_IMAGE_CAPTURE && resultCode == RESULT_OK ) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            electricity_reading_pic.setImageBitmap(imageBitmap);
        }
    }

}
