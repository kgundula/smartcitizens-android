package app.defensivethinking.co.za.smartcitizen.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import app.defensivethinking.co.za.smartcitizen.R;
import app.defensivethinking.co.za.smartcitizen.utility.utility;

public class ReadingsAdapter extends CursorAdapter {

    public static class ViewHolder {

        public final TextView account_numberView;
        public final TextView reading_dateView;
        public final TextView water_readingView;
        public final TextView electricity_readingView;

        public ViewHolder(View view) {

            account_numberView = (TextView) view.findViewById(R.id.account_number);
            reading_dateView = (TextView) view.findViewById(R.id.reading_date);
            water_readingView = (TextView) view.findViewById(R.id.water_reading);
            electricity_readingView = (TextView) view.findViewById(R.id.electricity_reading);

        }
    }

    public ReadingsAdapter ( Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.meter_reading;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String accountNumber = cursor.getString(1);
        String electricityReading = cursor.getString(2);
        String waterReading = cursor.getString(3);
        String readingsDate = utility.getDateFromISOFormat(cursor.getString(4));

        Log.i("Date", readingsDate);

        viewHolder.account_numberView.setText(accountNumber);
        viewHolder.reading_dateView.setText(readingsDate);
        viewHolder.water_readingView.setText(waterReading);
        viewHolder.electricity_readingView.setText(electricityReading);


    }
}
