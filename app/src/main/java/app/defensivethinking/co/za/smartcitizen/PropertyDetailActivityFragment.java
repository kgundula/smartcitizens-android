package app.defensivethinking.co.za.smartcitizen;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
    private TextView detail_property_bp;
    private TextView detail_property_portion;

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
        detail_property_bp = (TextView) rootView.findViewById(R.id.detail_property_bp);
        detail_property_portion = (TextView) rootView.findViewById(R.id.detail_property_portion);

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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(PropertyDetailActivity.ACCOUNT_KEY) ) {
            getLoaderManager().restartLoader(PROPERTY_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long _property_id = Long.valueOf(mAccountIdStr);
        Uri properrtByIduri = SmartCitizenContract.PropertyEntry.buildPropertyUri(_property_id);
        return new CursorLoader(getActivity(), properrtByIduri, null, null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            detail_property_account.setText(data.getString(2));
            detail_property_bp.setText("Property BP :"+data.getString(4));
            detail_property_portion.setText("Property Portion : "+data.getString(5));
            detail_property_contatct_tel.setText(data.getString(6));
            detail_property_initials.setText("");
            String name = data.getString(7)+" "+data.getString(9);
            detail_property_name.setText(name);
            detail_property_address.setText(data.getString(10));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
