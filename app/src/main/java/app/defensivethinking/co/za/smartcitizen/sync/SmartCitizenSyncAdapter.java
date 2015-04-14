package app.defensivethinking.co.za.smartcitizen.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import app.defensivethinking.co.za.smartcitizen.utility.utility;

/**
 * Created by Profusion on 2015-04-02.
 */
public class SmartCitizenSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SmartCitizenSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 1;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public SmartCitizenSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        if(utility.cookieManager == null)
            utility.cookieManager = new CookieManager();
        CookieHandler.setDefault(utility.cookieManager);



        final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
        final String SMART_CITIZEN_URL = "http://"+base_url+"/api/properties/owner/";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String propertyJsonStr = "";

        try {

            Uri builtUri = Uri.parse(SMART_CITIZEN_URL).buildUpon().build();


            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            //urlConnection.setFixedLengthStreamingMode(body.getBytes().length);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            //os.write(body.getBytes());
            //clean up
            //os.flush();

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
            Log.e(LOG_TAG, "Error ", e);
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
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        try {
            JSONObject propertyJson = new JSONObject(propertyJsonStr);

            Boolean success = propertyJson.getBoolean("success");
            if ( success ) {
                JSONObject properties = propertyJson.getJSONObject("properties");
                Log.i("Sync Adapter", properties.toString());


            }

        } catch (JSONException ex) {

        }



}

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
    }

    @Override
    public void onSyncCanceled(Thread thread) {
        super.onSyncCanceled(thread);
    }


    public static void initializeSyncAdapter(Context context) {
        //getSyncAccount(context);
    }
}
