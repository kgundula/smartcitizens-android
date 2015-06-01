package app.defensivethinking.co.za.smartcitizen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;
import app.defensivethinking.co.za.smartcitizen.service.SmartCitizenIntentService;
import app.defensivethinking.co.za.smartcitizen.utility.utility;


/**
 * A login screen that offers login via email/password.
 */
public class SmartCitizenLoginActivity extends Activity  {

    private String LOG_TAG = SmartCitizenLoginActivity.class.getSimpleName();

    private Pattern pattern;
    private Matcher matcher;

    private static final String email_reg_expr = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*\n"+"@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$;";

    private SmartCitizenIntentServiceReceiver receiver;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View mRegisterFormView;
    private EditText mRegisterEmailView;
    private EditText mRegisterPasswordView;
    private EditText mRegisterPasswordConfirmView;
    static View login_form, register_form;
    private static final String ACTIVE_VIEW_KEY = "active_view";
    private boolean isRegisterView = false;
    utility _utility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_citizen_login);

        IntentFilter filter = new IntentFilter(SmartCitizenIntentServiceReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new SmartCitizenIntentServiceReceiver();
        registerReceiver(receiver, filter);

        if (savedInstanceState !=null) {
            isRegisterView = savedInstanceState.getBoolean(ACTIVE_VIEW_KEY);
        }

        _utility = new utility(getApplicationContext());

        String username = "";
        String user  = getUser();

        try {
            if (user != null)
            {
                JSONObject userJson = new JSONObject(user);
                username = userJson.getString("email");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(username)) {

            register_form = findViewById(R.id.register_form);
            login_form = findViewById(R.id.login_form);

            pattern = Pattern.compile(email_reg_expr);
            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            mPasswordView = (EditText) findViewById(R.id.password);
            mProgressView = findViewById(R.id.login_progress);
            mRegisterEmailView  = (EditText) findViewById(R.id.register_email);
            mRegisterPasswordView     = (EditText) findViewById(R.id.register_password);
            mRegisterPasswordConfirmView = (EditText) findViewById(R.id.register_password2);
            mLoginFormView = findViewById(R.id.login_form);
            mRegisterFormView = findViewById(R.id.register_form);

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

                    TextView error_message = (TextView) findViewById(R.id.error_message);
                    error_message.setVisibility(View.INVISIBLE);
                    isRegisterView = true;
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

                    TextView error_message = (TextView) findViewById(R.id.error_message);
                    error_message.setVisibility(View.INVISIBLE);
                    isRegisterView = false;
                    login_form.setVisibility(View.VISIBLE);
                    register_form.setVisibility(View.GONE);
                    login_form.invalidate();
                    register_form.invalidate();
                }
            });



            if (isRegisterView) {
                login_form.setVisibility(View.GONE);
                register_form.setVisibility(View.VISIBLE);

            }
            else
            {
                login_form.setVisibility(View.VISIBLE);
                register_form.setVisibility(View.GONE);

            }


            register_form.invalidate();
            login_form.invalidate();

        }
        else
        {
            Intent intent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(SmartCitizenIntentServiceReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new SmartCitizenIntentServiceReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTIVE_VIEW_KEY, isRegisterView);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
    }

    public void attemptLogin() {

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
            mEmailView.setError(getResources().getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            TextView error_message = (TextView) findViewById(R.id.error_message);
            if (!_utility.isDeviceConnectedToInternet()) {
                updateErrorMessage(getResources().getString(R.string.no_internet));
            }
            else {
                hideErrorMessage();
                showProgress(true);

                final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
                final String SMART_CITIZEN_URL = "http://"+base_url+"/api/login?";

                Intent intent = new Intent(this, SmartCitizenIntentService.class);
                intent.putExtra("username",email);
                intent.putExtra("password",password);
                intent.putExtra("url",SMART_CITIZEN_URL);
                intent.putExtra("request_method", "login");
                startService(intent);

            }

        }
    }

    public void attemptRegister() {

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


            if (!_utility.isDeviceConnectedToInternet()) {

                updateErrorMessage(getResources().getString(R.string.no_internet));

            } else {
                showRegisterProgress(true);
                hideErrorMessage();
                final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
                final String SMART_CITIZEN_URL = "http://" + base_url + "/api/users?";

                Intent intent = new Intent(this, SmartCitizenIntentService.class);
                intent.putExtra("username",email);
                intent.putExtra("password",password);
                intent.putExtra("url",SMART_CITIZEN_URL);
                intent.putExtra("request_method", "register");
                startService(intent);

            }
        }

    }

    public void hideErrorMessage() {
        TextView error_message = (TextView) findViewById(R.id.error_message);
        error_message.setVisibility(View.GONE);
        error_message.invalidate();
    }

    public void updateErrorMessage(String text) {
        TextView error_message = (TextView) findViewById(R.id.error_message);
        error_message.setText(text);
        error_message.setTextColor(getResources().getColor(R.color.smart_citizen_text_color));
        error_message.setBackgroundColor(getResources().getColor(R.color.red_500));
        error_message.setVisibility(View.VISIBLE);
        error_message.invalidate();
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

    public class SmartCitizenIntentServiceReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "app.defensivethinking.co.za.smartcitizen.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseString = intent.getStringExtra(SmartCitizenIntentService.RESPONSE_STRING);
            int reponseStatus = intent.getIntExtra(SmartCitizenIntentService.STATUS, 0);
            String request_method = intent.getStringExtra(SmartCitizenIntentService.REQUEST_METHOD);

            if ( reponseStatus == 1 ) {

                if (request_method.equals("login")) {
                    showProgress(false);

                    try {
                        JSONObject my_json = new JSONObject(responseString);

                        if ( my_json.has("success") && my_json.getString("success").equals("false")) {
                           updateErrorMessage(my_json.getString("message"));

                        } else {

                            getUserDataFromJson(responseString);
                            Intent loginIntent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
                            startActivity(loginIntent);
                            finish();
                        }

                    } catch (JSONException e) {
                        Toast.makeText( context , "OOPS! - "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else if (request_method.equals("register")) {
                    showRegisterProgress(false);
                    try {
                        JSONObject my_json = new JSONObject(responseString);

                        if (my_json.getString("success").equals("false")) {
                            updateErrorMessage(my_json.getString("message"));
                        } else {
                            JSONObject user = my_json.getJSONObject("user");
                            saveUser(user);
                            String username = user.getString("username");
                            String updated  = user.getString("updated");
                            String email    = user.getString("email");
                            String hash     = user.getString("hash");
                            String salt     = user.getString("salt");
                            String _ID      = user.getString("_id");

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
                                Toast.makeText( context , "OOPS! - "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            Intent registerIntent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
                            startActivity(registerIntent);
                            finish();
                        }

                    } catch (JSONException e) {
                        Toast.makeText( context , "OOPS! - "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            } else if ( reponseStatus == 0 ) {
                Toast.makeText( context , "OOPS! Something went wrong with the app, please retry.", Toast.LENGTH_LONG).show();

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
        return settings.getString("user", "");
    }


}



