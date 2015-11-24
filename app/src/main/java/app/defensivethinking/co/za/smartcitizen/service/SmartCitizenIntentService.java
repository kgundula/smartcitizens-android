package app.defensivethinking.co.za.smartcitizen.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

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

import app.defensivethinking.co.za.smartcitizen.SmartCitizenLoginActivity;
import app.defensivethinking.co.za.smartcitizen.utility.utility;

public class SmartCitizenIntentService extends IntentService {

    private static final String LOG_TAG = SmartCitizenIntentService.class.getSimpleName();

    public static final int STATUS_FINISHED = 1;

    public static final String STATUS = "status";
    public static final String RESPONSE_STRING = "response_string";
    public static final String REQUEST_METHOD  = "request_method";

    public SmartCitizenIntentService() {
        super(SmartCitizenIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String userJsonStr = "";

        String SMART_CITIZEN_URL = intent.getStringExtra("url");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String request_method = intent.getStringExtra("request_method");

        final String EMAIL_PARAM = "email";
        final String USERNAME_PARAM = "username";
        final String PASSWORD_PARAM = "password";

        try {

            if(utility.cookieManager == null)
                utility.cookieManager = new CookieManager();
            CookieHandler.setDefault(utility.cookieManager);

            if ( request_method.equals("register") )  {
                JSONObject user_reg = new JSONObject();

                try {

                    user_reg.put(EMAIL_PARAM, username);
                    user_reg.put(USERNAME_PARAM, username);
                    user_reg.put(PASSWORD_PARAM, password);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Uri builtUri = Uri.parse(SMART_CITIZEN_URL)
                        .buildUpon()
                        .build();

                String body = user_reg.toString();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(body.getBytes().length);

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(body.getBytes());
                os.flush();

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
                userJsonStr = buffer.toString();


            }
            else if( request_method.equals("login") )
            {

                Uri builtUri = Uri.parse(SMART_CITIZEN_URL).buildUpon()
                        .appendQueryParameter(USERNAME_PARAM, username)
                        .appendQueryParameter(PASSWORD_PARAM, password)
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
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

                userJsonStr = buffer.toString();
            }

        } catch (IOException e) {
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Toast.makeText(getApplicationContext(), "OOPS! - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SmartCitizenLoginActivity.SmartCitizenIntentServiceReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_STRING, userJsonStr);
        broadcastIntent.putExtra(STATUS, STATUS_FINISHED);
        broadcastIntent.putExtra(REQUEST_METHOD, request_method);
        sendBroadcast(broadcastIntent);
    }

}
