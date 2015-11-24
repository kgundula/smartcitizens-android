package app.defensivethinking.co.za.smartcitizen.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import app.defensivethinking.co.za.smartcitizen.R;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.utility.utility;

public class SmartCitizenSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SmartCitizenSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 360; //
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public SmartCitizenSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);

        final String base_url = utility.base_url; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/properties/owner/ownerId";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String propertyJsonStr = "";

        try {

            Uri builtUri = Uri.parse(SMART_CITIZEN_URL).buildUpon().build();

            JSONObject body_json = new JSONObject();
            String mEmail = getUsername();
            try {
                body_json.put("email", mEmail);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String body = body_json.toString();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();


            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }

            propertyJsonStr = buffer.toString();

        } catch (IOException e) {
            Toast.makeText(getContext(), "OOPS! Something went wrong with the app, please retry.", Toast.LENGTH_LONG).show();
            return;
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Toast.makeText(getContext(), "OOPS! Something went wrong with the app, please retry.", Toast.LENGTH_LONG).show();
                }
            }
        }


        try {
            JSONObject propertyJson = new JSONObject(propertyJsonStr);

            Boolean success = propertyJson.getBoolean("success");
            if ( success ) {

                JSONArray properties = propertyJson.getJSONArray("properties");

                Vector<ContentValues> cVVector = new Vector<>(properties.length());

                for (int i = 0; i < properties.length(); i++ ) {

                    JSONObject property = properties.getJSONObject(i);

                    String _id = property.getString("_id");
                    String contact_tel = property.getString("contacttel");
                    String bp          = property.getString("bp");
                    String physical_address = property.getString("physicaladdress");
                    String property_updated = property.getString("updated");
                    String initials = property.getString("initials");
                    String property_email   = property.getString("email");
                    String owner = property.getString("owner");
                    String surname = property.getString("surname");
                    String account_number = property.getString("accountnumber");
                    String portion = property.getString("portion");


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

                    cVVector.add(propertyValues);

                }

                if ( cVVector.size() > 0 ) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    try {
                        getContext().getContentResolver().bulkInsert(SmartCitizenContract.PropertyEntry.CONTENT_URI, cvArray);
                    } catch (Exception e) {
                        //Toast.makeText(getContext(), "OOPS! - "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    getContext().getContentResolver().notifyChange(SmartCitizenContract.PropertyEntry.CONTENT_URI, null);
                }
            }

        } catch (JSONException ex) {
            //Toast.makeText(getContext(), "OOPS! - "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public String getUsername() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());//getApplicationContext());
        String user = settings.getString("user", "");
        String username = "";
        try {
            JSONObject userObject = new JSONObject(user);
            username = userObject.getString("username");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return username;
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
    }

    @Override
    public void onSyncCanceled(Thread thread) {
        super.onSyncCanceled(thread);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
    }

    public static Account getSyncAccount(Context context) {

        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        SmartCitizenSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
