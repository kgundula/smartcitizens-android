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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;


/**
 * A login screen that offers login via email/password.
 */
public class SmartCitizenLoginActivity extends Activity  {

    private String LOG_TAG = SmartCitizenLoginActivity.class.getSimpleName();

    private Pattern pattern;
    private Matcher matcher;

    private static Boolean remember_me = false;
    private static final String email_reg_expr = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*\n"+"@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$;";


   /**
    * A dummy authentication store containing known user names and passwords.
    * TODO: remove after connecting to a real authentication system.
    */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
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

        String username = getUsername();
        String password = getPassword();
        Log.i("Username", username);
        Log.i("Passord", password);

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
            mProgressView = findViewById(R.id.login_progress);
            mRegisterFormView = findViewById(R.id.register_form);

        }
        else
        {
            remember_me = true;
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
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

    private boolean isEmailValid(String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
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

            if (success) {

                savePassword(mPassword);
                Intent intent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
                startActivity(intent);
                finish();

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mUsername;

        UserRegisterTask(String email, String username ,String password) {
            mEmail = email;
            mPassword = password;
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String userJsonStr = "";

            try {

                final String base_url = "smartcitizen.defensivethinking.co.za"; // dev smart citizen
                final String SMART_CITIZEN_URL = "http://" + base_url + "/users?";
                final String EMAIL_PARAM = "email";
                final String USERNAME_PARAM = "username";
                final String PASSWORD_PARAM = "password";

                Uri builtUri = Uri.parse(SMART_CITIZEN_URL).buildUpon()
                        .appendQueryParameter(EMAIL_PARAM, mEmail)
                        .appendQueryParameter(USERNAME_PARAM, mUsername)
                        .appendQueryParameter(PASSWORD_PARAM, mPassword)
                        .build();
                Log.i("url", builtUri.toString());
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
                userJsonStr = buffer.toString();

            }
            catch (IOException e) {
                return false;
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

            Log.i("User ",userJsonStr);
            /*
            try {
               getUserDataFromJson(userJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            */
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;

            showRegisterProgress(false);

            if (success) {

                Log.i("Riley", "Was her");
               // savePassword(mPassword);
               // Intent intent = new Intent(SmartCitizenLoginActivity.this, SmartCitizenMainActivity.class);
               // startActivity(intent);
               // finish();

            } else {

                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

    }

    public void getUserDataFromJson(String userJsonStr) throws JSONException {

        try {

            JSONObject jsonObj =  new JSONObject(userJsonStr);

            JSONObject user = jsonObj.getJSONObject("user");
            JSONArray properties = jsonObj.getJSONArray("properties");
            // Log.i("user",userJsonStr);

            //  {"username":"kgundula@defensivethinking.co.za",
            //   "_id":"54fb13153a4e22ad758ccfdf",
            //   "updated":"2015-03-07T15:02:45.588Z",
            //   "email":"kgundula@defensivethinking.co.za",
            //   "hash":"17a389ccb9b6fae491321809b656ea41902f586aae3a0fc1ca0ef6954d6528ff97db3c3a378d022fc8343bee664264f7c986ea6723991793a6404433a1a92a62fd4c0f7c2402193cdc0c20a3c30809ed39149f25df22bb21722ff3e04a4de8cb3e3edb8d8595b33546456b66bbd7592e11b550a29cbf43ce1938189473528602c824f0a1ba289fa0f661e9a3737c7bdaae622d965dd2290d608d146e429dba5e58d4e05ad521b13fd7c63c78ebe7dab5e6a82e83d253eee90930feff8118a0abee3568d40e7200e4a6a934905b5206ad2cf554ee2352d4e9ecd71e4b19d79d5bc110d28bf365397f900095a2f0caa53d63d766f45cd5ec2a5e6b7a55f1bb96b599dc2577f1d24b6317a2e0ba421f4b44ee5138deb0161e93a6c9fc59e287e54cd544b4ab75a0b4d4358c43140149ab78af17a62cf21015315bc55c3ed137ebd1dc21452a2f462580dbb38f0b59c4402d15fae716a666e51c8d9e72d909ce26ce8d28d365ec4ac9986f745a92ee639e613f07c276bcf3b39bab4e67142e32b641c5dec5cbd89706e9bf425729c93eea82dc6b22664c3b00bc52bf54a2800e743609213ef14ed7766a3ec8abd84ebc8a3e8d09412ebe149b701ab51f2f002cc0e1df90a904c05bd85a961693f3a4b3d1d2a5c2f948e58a18632ec555b7c79fb1881e705c69b9afcddedc7c74d1b4edd760cd415fbc724c111d5fab8ca01135a2e7",
            //   "__v":0,"password":"this-is-not-it",
            //   "salt":"9682d49428477dbc17fafcce3c7ea204817e5cb17941c263692a4be9226186b6"}

            // {"user":{"password":"this-is-not-it","_id":"54fb13153a4e22ad758ccfdf","salt":"9682d49428477dbc17fafcce3c7ea204817e5cb17941c263692a4be9226186b6","hash":"17a389ccb9b6fae491321809b656ea41902f586aae3a0fc1ca0ef6954d6528ff97db3c3a378d022fc8343bee664264f7c986ea6723991793a6404433a1a92a62fd4c0f7c2402193cdc0c20a3c30809ed39149f25df22bb21722ff3e04a4de8cb3e3edb8d8595b33546456b66bbd7592e11b550a29cbf43ce1938189473528602c824f0a1ba289fa0f661e9a3737c7bdaae622d965dd2290d608d146e429dba5e58d4e05ad521b13fd7c63c78ebe7dab5e6a82e83d253eee90930feff8118a0abee3568d40e7200e4a6a934905b5206ad2cf554ee2352d4e9ecd71e4b19d79d5bc110d28bf365397f900095a2f0caa53d63d766f45cd5ec2a5e6b7a55f1bb96b599dc2577f1d24b6317a2e0ba421f4b44ee5138deb0161e93a6c9fc59e287e54cd544b4ab75a0b4d4358c43140149ab78af17a62cf21015315bc55c3ed137ebd1dc21452a2f462580dbb38f0b59c4402d15fae716a666e51c8d9e72d909ce26ce8d28d365ec4ac9986f745a92ee639e613f07c276bcf3b39bab4e67142e32b641c5dec5cbd89706e9bf425729c93eea82dc6b22664c3b00bc52bf54a2800e743609213ef14ed7766a3ec8abd84ebc8a3e8d09412ebe149b701ab51f2f002cc0e1df90a904c05bd85a961693f3a4b3d1d2a5c2f948e58a18632ec555b7c79fb1881e705c69b9afcddedc7c74d1b4edd760cd415fbc724c111d5fab8ca01135a2e7","username":"kgundula@defensivethinking.co.za","email":"kgundula@defensivethinking.co.za","__v":0,"updated":"2015-03-07T15:02:45.588Z"},
            // "properties":[
            // {"_id":"54fb1456e5694d63761d65e9","portion":"A2","accountnumber":"123456789","bp":"01","contacttel":"0721234567","email":"kgundula@defensivethinking.co.za","initials":"K","surname":"Gundula","physicaladdress":"94043 Mountain View","owner":"54fb13153a4e22ad758ccfdf","__v":0,"updated":"2015-03-07T15:08:06.140Z"},
            // {"_id":"550d5b94e5694d63761d65ed","portion":"B11","accountnumber":"23142341234","bp":"02","contacttel":"0721235656","email":"kgundula@defensivethinking.co.za","initials":"KG","surname":"Gundula","physicaladdress":"12 Terrance Woods Drive ","owner":"54fb13153a4e22ad758ccfdf","__v":0,"updated":"2015-03-21T11:52:52.384Z"}]}
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
                saveUsername(username);
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

                //Log.i("property", property.toString());
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

    public void savePassword(String password) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("password", password);
        editor.commit();
    }

    public void saveUsername (String username) {
        Log.i("User", username);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.commit();
    }

    public String getUsername() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString("username", "");
        return username;
    }

    public String getPassword() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString("password", "");
        return username;
    }

}



