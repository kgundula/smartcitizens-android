package app.defensivethinking.co.za.smartcitizen.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import app.defensivethinking.co.za.smartcitizen.R;
import app.defensivethinking.co.za.smartcitizen.SmartCitizenMainFragment;


public class PropertyAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView accountNumberView;
        public final TextView physicalAddressView;


        public ViewHolder(View view) {

            accountNumberView = (TextView) view.findViewById(R.id.account_number);
            physicalAddressView = (TextView) view.findViewById(R.id.physical_address);

        }
    }

    public PropertyAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.row_card;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read date from cursor
        String accountNumber = cursor.getString(SmartCitizenMainFragment.COL_ACCOUNT_NUMBER);
        // Find TextView and set formatted date on it
        viewHolder.accountNumberView.setText(accountNumber);

        // Read weather forecast from cursor
        String physicalAddress = cursor.getString(SmartCitizenMainFragment.COL_PHYSICAL_ADDRESS);
        // Find TextView and set weather forecast on it
        viewHolder.physicalAddressView.setText(physicalAddress);
    }


}