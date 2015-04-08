package app.defensivethinking.co.za.smartcitizen;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Profusion on 2015-04-08.
 */
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

        String accountNumber = "";
        String readingsDate = "";
        String waterReading = "";
        String electricityReading = "";

        viewHolder.account_numberView.setText(accountNumber);
        viewHolder.reading_dateView.setText(readingsDate);
        viewHolder.water_readingView.setText(waterReading);
        viewHolder.electricity_readingView.setText(electricityReading);


    }
}
