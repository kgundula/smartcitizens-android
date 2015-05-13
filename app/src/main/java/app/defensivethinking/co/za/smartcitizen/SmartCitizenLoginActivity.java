package app.defensivethinking.co.za.smartcitizen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.utility.utility;


/**
 * A login screen that offers login via email/password.
 */
public class SmartCitizenLoginActivity extends Activity  {

    private String LOG_TAG = SmartCitizenLoginActivity.class.getSimpleName();

    private Pattern pattern;
    private Matcher matcher;

    private static Boolean remember_me = false;
    private static final String email_reg_expr = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*\n"+"@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$;";

    private UserLoginTask mAuthTask = null;
    private UserRegisterTask mRegisterTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mRegisterFormView;
    private EditText mRegisterEmailView;
    private EditText mRegisterPasswordView;
    private EditText mRegisterPasswordConfirmView;
    static View login_form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_citizen_login);

        String username = "";

        String user  = getUser();
        Log.i("user", user);
        try {

            JSONObject userJson = new JSONObject(user);
            username = userJson.getString("email");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        if (TextUtils.isEmpty(username)) {

            remember_me = false;
            final View register_form = (View) findViewById(R.id.register_form);
            login_form = (View) findViewById(R.id.login_form);

            pattern = Pattern.compile(email_reg_expr);
            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            mPasswordView = (EditText) findViewById(R.id.password);

            mRegisterEmailView  = (EditText) findViewById(R.id.register_email);
            mRegisterPasswordView     = (EditText) findViewById(R.id.register_password);
            mRegisterPasswordConfirmView = (EditText) findViewById(R.id.register_password2);

            mRegisterEmailView = (EditText) findViewById(R.id.register_email);

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            Button mRegisterButton = (Button) findViewById(R.id.email_register_button);
            mRegisterButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    login_form.setVisibility(View.GONE);
                    register_form.setVisibility(View.VISIBLE);
                    login_form.invalidate();
                    register_form.invalidate();
                }
            });

            Button mSignButton = (Button) findViewById(R.id.email_register);
            mSignButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    attemptRegister();
                }
            });

            Button mLogin = (Button) findViewById(R.id.email_sign_in);
            mLogin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    login_form.setVisibility(View.VISIBLE);
                    register_form.setVisibility(View.GONE);
                    login_form.invalidate();
                    register_form.invalidate();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.invalidate();
            mProgressView = findViewById(R.id.login_progress);
            mRegisterFormView = findViewById(R.id.register_form);

        }
        else
        {
            remember_me = true;
            Intent intent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;


        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    public void attemptRegister() {

        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mRegisterEmailView .setError(null);
        mRegisterPasswordView.setError(null);
        mRegisterPasswordConfirmView.setError(null);


        String email = mRegisterEmailView.getText().toString().trim();
        String password = mRegisterPasswordView.getText().toString().trim();
        String password2 = mRegisterPasswordConfirmView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mRegisterEmailView.setError(getString(R.string.error_field_required));
            focusView = mRegisterEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mRegisterPasswordView.setError(getString(R.string.error_field_required));
            focusView = mRegisterPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password2) ){
            mRegisterPasswordConfirmView.setError(getString(R.string.error_field_required));
            focusView = mRegisterPasswordConfirmView;
            cancel = true;

        }

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mRegisterPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mRegisterPasswordView;
            cancel = true;
        }


        if (!TextUtils.isEmpty(password2) && !isPasswordValid(password2)) {
            mRegisterPasswordConfirmView.setError(getString(R.string.error_invalid_password));
            focusView = mRegisterPasswordConfirmView;
            cancel = true;
        }

        if ( !password.equals(password2)) {
            mRegisterPasswordConfirmView.setError(getString(R.string.error_password_match));
            focusView = mRegisterPasswordConfirmView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showRegisterProgress(true);
            String username = email;
            mRegisterTask = new UserRegisterTask(email, username , password);
            mRegisterTask.execute((Void) null);

        }

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showRegisterProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mRegisterFormView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String userJsonStr = "";

            try {

                final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
                final String SMART_CITIZEN_URL = "http://"+base_url+"/api/login?";
                final String USERNAME_PARAM = "username";
                final String PASSWORD_PARAM = "password";

                if(utility.cookieManager == null)
                    utility.cookieManager = new CookieManager();
                CookieHandler.setDefault(utility.cookieManager);

                Uri builtUri = Uri.parse(SMART_CITIZEN_URL).buildUpon()
                        .appendQueryParameter(USERNAME_PARAM, mEmail)
                        .appendQueryParameter(PASSWORD_PARAM, mPassword)
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return false;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return false;
                }
                userJsonStr = buffer.toString();
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
                getUserDataFromJson(userJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (!remember_me) {
                showProgress(false);
            }

            TextView error_message = (TextView) findViewById(R.id.error_message);
            if (success) {

                Intent intent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
                startActivity(intent);
                finish();

            } else {
                error_message.setText(getString(R.string.error_incorrect_password));
                error_message.setTextColor(getResources().getColor(R.color.smart_citizen_text_color));
                error_message.setBackgroundColor(getResources().getColor(R.color.red_500));
                error_message.setVisibility(View.VISIBLE);
                error_message.invalidate();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mUsername;

        UserRegisterTask(String email, String username, String password) {
            mEmail = email;
            mPassword = password;
            mUsername = username;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String userJsonStr = "";

            try {

                final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
                final String SMART_CITIZEN_URL = "http://" + base_url + "/api/users?";
                final String EMAIL_PARAM = "email";
                final String USERNAME_PARAM = "username";
                final String PASSWORD_PARAM = "password";

                JSONObject user_reg = new JSONObject();

                try {

                    user_reg.put(EMAIL_PARAM, mEmail);
                    user_reg.put(USERNAME_PARAM, mUsername);
                    user_reg.put(PASSWORD_PARAM, mPassword);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                Uri builtUri = Uri.parse(SMART_CITIZEN_URL).buildUpon()

                        .build();

                if (utility.cookieManager == null)
                    utility.cookieManager = new CookieManager();
                CookieHandler.setDefault(utility.cookieManager);
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
                //clean up
                os.flush();

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
                userJsonStr = buffer.toString();

            } catch (IOException e) {
                JSONObject json = new JSONObject();
                try {

                    json.put("success", "false");
                    json.put("message", e.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return json.toString();
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

            return userJsonStr;
        }

        @Override
        protected void onPostExecute(final String result) {
            mRegisterTask = null;
            Boolean success = false;
            showRegisterProgress(false);
            try {
                JSONObject my_json = new JSONObject(result);
                Log.i("new user", my_json.toString());

                JSONObject user = my_json.getJSONObject("user");
                Log.i("reg user", user.toString());
                saveUser(user);

                TextView error_message = (TextView) findViewById(R.id.error_message);
                success = Boolean.valueOf(my_json.getString("success"));
                if (my_json.getString("success").equals("false")) {

                    error_message.setText(my_json.getString("message"));
                    error_message.setTextColor(getResources().getColor(R.color.smart_citizen_text_color));
                    error_message.setBackgroundColor(getResources().getColor(R.color.red_500));
                    error_message.setVisibility(View.VISIBLE);
                    error_message.invalidate();

                } else {

                    Intent intent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


        }

    }

    public void getUserDataFromJson(String userJsonStr) throws JSONException {

        try {

            JSONObject jsonObj =  new JSONObject(userJsonStr);

            JSONObject user = jsonObj.getJSONObject("user");
            JSONArray properties = jsonObj.getJSONArray("properties");
            saveUser(user);

            String username = user.getString("username");
            String updated  = user.getString("updated");
            String email    = user.getString("email");
            String hash     = user.getString("hash");
            String salt     = user.getString("salt");
            String _ID      = user.getString("_id");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(properties.length());

            ContentValues userValues = new ContentValues();

            userValues.put(SmartCitizenContract.UserEntry.COLUMN_USER_ID, _ID);
            userValues.put(SmartCitizenContract.UserEntry.COLUMN_USER_EMAIL, email);
            userValues.put(SmartCitizenContract.UserEntry.COLUMN_USERNAME, username);
            userValues.put(SmartCitizenContract.UserEntry.COLUMN_USER_HASH, hash);
            userValues.put(SmartCitizenContract.UserEntry.COLUMN_USER_SALT, salt);
            userValues.put(SmartCitizenContract.UserEntry.COLUMN_UPDATED, updated);

            try {
                getContentResolver().insert(SmartCitizenContract.UserEntry.CONTENT_URI, userValues);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
            for(int i = 0; i < properties.length(); i++) {

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
                    getContentResolver().bulkInsert(SmartCitizenContract.PropertyEntry.CONTENT_URI, cvArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getContentResolver().notifyChange(SmartCitizenContract.PropertyEntry.CONTENT_URI, null);
            }
        } catch (NullPointerException nex) {
            nex.printStackTrace();
        }

    }

    public void saveUser (JSONObject userJson ) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user", userJson.toString());
        editor.commit();
    }

    public String getUser() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = settings.getString("user", "");
        return user;
    }


}



