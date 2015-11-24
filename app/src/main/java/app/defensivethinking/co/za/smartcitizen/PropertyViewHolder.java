package app.defensivethinking.co.za.smartcitizen;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class PropertyViewHolder extends RecyclerView.ViewHolder {

    protected TextView account_number;
    protected TextView physical_address;

    public PropertyViewHolder(View view) {
        super(view);
        this.account_number = (TextView) view.findViewById(R.id.account_number);
        this.physical_address = (TextView) view.findViewById(R.id.physical_address);
    }
}
