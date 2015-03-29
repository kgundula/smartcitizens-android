package app.defensivethinking.co.za.smartcitizen;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PropertyActivity extends ActionBarActivity {

   private static final String LOG_TAG = PropertyActivity.class.getSimpleName();

   private static EditText property_account, property_address, property_bp, property_contact,
            property_initials, property_surname, property_portion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);

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

                    addProperty(portion, bp, initials, surname, contact, address, account);
                }
            }
        });
    }

    public void addProperty(String portion, String bp, String initials, String surname, String contact, String address, String account) {


    }

    public class UserPropertyTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mOwner;
        private final String mPortion;
        private final String mBp;
        private final String mAccount;
        private final String mAddress;
        private final String mContact;
        private final String mInitials;
        private final String mSurname;

        UserPropertyTask(String email, String owner, String portion, String bp, String account, String address, String contact, String initials, String surname ) {
            mEmail = email;
            mOwner = owner;
            mPortion = portion;
            mBp = bp;
            mAccount  = account;
            mAddress = address;
            mContact = contact;
            mInitials = initials;
            mSurname = surname;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String propertyJsonStr = "";

            try {

                final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
                final String SMART_CITIZEN_URL = "http://"+base_url+"/api/addproperty?";
                final String SURNAME_PARAM     = "surname";
                final String INITIALS_PARAM    = "initials";
                final String EMAIL_PARAM       = "email";
                final String ACCOUNT_PARAM     = "account";
                final String OWNER_PARAM       = "owner";
                final String PORTION_PARAM     = "portion";
                final String BP_PARAM          = "bp";
                final String CONTACT_PARAM     = "contact";
                final String ADDRESS_PARAM     = "address";



                Uri builtUri = Uri.parse(SMART_CITIZEN_URL).buildUpon()
                        .appendQueryParameter(SURNAME_PARAM, mSurname)
                        .appendQueryParameter(INITIALS_PARAM, mInitials)
                        .appendQueryParameter(EMAIL_PARAM, mEmail)
                        .appendQueryParameter(ACCOUNT_PARAM, mAccount)
                        .appendQueryParameter(OWNER_PARAM, mOwner)
                        .appendQueryParameter(PORTION_PARAM, mPortion)
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                propertyJsonStr = buffer.toString();

            } catch (IOException e) {
                return false;
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                //addProperty(userJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_property, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
