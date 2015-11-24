package app.defensivethinking.co.za.smartcitizen;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import app.defensivethinking.co.za.smartcitizen.adapter.PropertyAdapter;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.PropertyEntry;
import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract.UserEntry;


public class SmartCitizenMainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = SmartCitizenMainFragment.class.getSimpleName();
    public Context context;
    private PropertyAdapter mPropertyAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    public static String mTwoPaneKey = "mTwoPane";
    private boolean mTwoPane;
    private static final int PROPERTY_LOADER = 0;

    private static final String[] PROJECTION = new String[]{PropertyEntry._ID,
            PropertyEntry.COLUMN_PROPERTY_CONTACT_TEL,
            PropertyEntry.COLUMN_PROPERTY_PHYSICAL_ADDRESS,
            PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER};

    private static final String[] USER_PROJECTION = new String[] {UserEntry.COLUMN_USER_ID,
            UserEntry.COLUMN_USER_EMAIL,
            UserEntry.COLUMN_USERNAME,
            UserEntry.COLUMN_UPDATED
    };

    private static String PROPERTY_SELECTED_KEY = "selected_position";

    public static final int COL_PROPERTY_ID = 0;
    public static final int COL_PHYSICAL_ADDRESS = 2;
    public static final int COL_ACCOUNT_NUMBER = 3;

    Cursor property_cursor, user_cursor;

    public static String property_owner = "";
    public static String user_email = "";
    private MyMainActivityInterface myMainActivityInterface;

    public SmartCitizenMainFragment() {
        setHasOptionsMenu(true);
    }

    public interface MyMainActivityInterface {
        void MainActivityData (String user_email,String property_owner);
    }

    public interface Callback {
         void onItemSelected(String property_id);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //context = (Activity) context;
        myMainActivityInterface =(MyMainActivityInterface)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final String email_address = getUsername();
        mPropertyAdapter = new PropertyAdapter(getActivity(), null,0);
        View rootView = inflater.inflate(R.layout.fragment_smart_citizen_main, container, false);
        TextView welcome_email_text = (TextView) rootView.findViewById(R.id.welcome_email_text);

        welcome_email_text.setText(email_address);
        String userSelection = "(" + UserEntry.COLUMN_USER_EMAIL + " = ? )";
        String[] userSelectAgs = new String[] {email_address};

        user_cursor =  getActivity().getContentResolver().query(UserEntry.CONTENT_URI , USER_PROJECTION, userSelection, userSelectAgs, null);

        if ( user_cursor != null && user_cursor.moveToFirst() ) {
            property_owner = user_cursor.getString(0);
            user_email  = user_cursor.getString(1);
            myMainActivityInterface.MainActivityData(user_email,property_owner);
        }

        ImageView imgCaptureReading = (ImageView) rootView.findViewById(R.id.imgCaptureReading);
        imgCaptureReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureReading();
            }
        });

        TextView txtCaptureReading = (TextView) rootView.findViewById(R.id.txtCaptureReading);
        txtCaptureReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureReading();
            }
        });


        ImageView imgViewReading = (ImageView) rootView.findViewById(R.id.imgViewReading);
        imgViewReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewReading();
            }
        });

        TextView txtViewReading = (TextView) rootView.findViewById(R.id.txtViewReading);
        txtViewReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewReading();
            }
        });

        ImageView imgNotification = (ImageView) rootView.findViewById(R.id.imgNotification);
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification();
            }
        });

        TextView txtNotification = (TextView) rootView.findViewById(R.id.txtNotification);
        txtNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification();
            }
        });

        mListView = (ListView) rootView.findViewById(R.id.listview_properties);
        mListView.setAdapter(mPropertyAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(PROPERTY_SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(PROPERTY_SELECTED_KEY);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(COL_PROPERTY_ID));
                }
            }
        });

        user_cursor.close();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PROPERTY_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PROPERTY_LOADER, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(PROPERTY_SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = PropertyEntry.COLUMN_PROPERTY_ACCOUNT_NUMBER + " ASC";

        Uri property_uri = PropertyEntry.CONTENT_URI;
        String propertySelection = "(" + PropertyEntry.COLUMN_PROPERTY_OWNER + " = ? )";
        String[] propertySelectAgs = new String[] {property_owner};

        return new CursorLoader(
                getActivity(),
                property_uri,
                PROJECTION,
                propertySelection,
                propertySelectAgs,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPropertyAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPropertyAdapter.swapCursor(null);
    }

    public void Notification() {
        Intent intent = new Intent(getActivity(), NotificationsActivity.class);
        startActivity(intent);
    }

    public void CaptureReading () {
        Intent intent = new Intent(getActivity(), CaptureReadingActivity.class);
        intent.putExtra("user_email", user_email);
        intent.putExtra("property_owner", property_owner);
        startActivity(intent);

    }

    public void ViewReading() {
        Intent intent = new Intent(getActivity(), ViewReadingActivity.class);
        intent.putExtra("property_owner", property_owner);
        startActivity(intent);
    }

    public String getUsername() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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

}
