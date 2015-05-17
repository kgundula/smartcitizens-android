package app.defensivethinking.co.za.smartcitizen;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.defensivethinking.co.za.smartcitizen.data.SmartCitizenContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class PropertyDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = PropertyDetailActivityFragment.class.getSimpleName();

    private String mAccountIdStr;
    private static final int PROPERTY_DETAIL_LOADER = 0;

    private TextView detail_property_account;
    private TextView detail_property_initials;
    private TextView detail_property_name;
    private TextView detail_property_contatct_tel;
    private TextView detail_property_address;


    public PropertyDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mAccountIdStr = arguments.getString(PropertyDetailActivity.ACCOUNT_KEY);
        }

        View rootView  = inflater.inflate(R.layout.fragment_property_detail, container, false);

        detail_property_account = (TextView) rootView.findViewById(R.id.detail_property_account);
        detail_property_initials = (TextView) rootView.findViewById(R.id.detail_property_initials);
        detail_property_name = (TextView) rootView.findViewById(R.id.detail_property_name);
        detail_property_contatct_tel = (TextView) rootView.findViewById(R.id.detail_property_contatct_tel);
        detail_property_address = (TextView) rootView.findViewById(R.id.detail_property_address);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PropertyDetailActivity.ACCOUNT_KEY)) {
            getLoaderManager().initLoader(PROPERTY_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PropertyDetailActivity.ACCOUNT_KEY) ) {
           // getLoaderManager().restartLoader(PROPERTY_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long _property_id = Long.valueOf(mAccountIdStr);
        Uri properrtByIduri = SmartCitizenContract.PropertyEntry.buildPropertyUri(_property_id);
        Log.i(LOG_TAG, properrtByIduri.toString());

        return new CursorLoader(getActivity(), properrtByIduri, null, null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.i(LOG_TAG, DatabaseUtils.dumpCursorToString(data));
            /*
            *   _id=1
                property_id=555719a72589f0903c74b804
                property_account_number=4002356325
                property_email=kgundula@defensivethinking.co.za
                property_bp=10
                property_portion=B10
                property_contact_tel=0721235656
                property_initials=K
                property_owner=555719302589f0903c74b803
                property_surname=Gundula
                property_physical_address=63 Shuttleworth Street
                property_updated=2015-05-16T10:19:19.351Z

            * */
            detail_property_account.setText(data.getString(2));
            detail_property_contatct_tel.setText(data.getString(6));
            detail_property_initials.setText(data.getString(7));
            detail_property_name.setText(data.getString(9));
            detail_property_address.setText(data.getString(10));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
