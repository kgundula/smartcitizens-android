package app.defensivethinking.co.za.smartcitizen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Profusion on 2015-03-11.
 */
public class PropertyAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

    private List<Property> propertyList;
    private Context mContext;

    PropertyAdapter(Context context, List<Property> propList) {
        this.propertyList = propList;
        this.mContext = context;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder ( ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_card, null);
        PropertyViewHolder viewHolder = new PropertyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder propertyListRowHolder, int i) {
        Property mProperty = propertyList.get(i);

        propertyListRowHolder.account_number.setText(mProperty.getAccountNumber());
        propertyListRowHolder.physical_address.setText(mProperty.getPropertyPhysicalAddress());

    }

    @Override
    public int getItemCount() {
        return (null != propertyList ? propertyList.size() : 0);
    }

    /**
     * Created by Profusion on 2015-03-21.
     */
    public static class Property {

        private int property_id;
        private String property_account_number;
        private String property_portion;
        private String property_contact_tel;
        private String property_email;
        private String property_initials;
        private String property_surname;
        private String property_physical_address;

        Property() {

            this.property_id = 0;
            this.property_account_number = "";
            this.property_portion = "";
            this.property_contact_tel = "";
            this.property_email = "";
            this.property_initials = "";
            this.property_surname = "";
            this.property_physical_address = "";
        }

        public int getPropertyID() {
            return property_id;
        }

        public void setPropertyID (int property_id) {
            this.property_id = property_id;
        }

        public String getAccountNumber() {
            return property_account_number;
        }

        public void setAccountNumber(String account_number) {
            this.property_account_number = account_number;
        }

        public String getPropertyPortion() {
            return property_portion;
        }

        public void setPropertyPortion ( String property_portion ) {
            this.property_portion = property_portion;
        }

        public String getPropertyContacttel () {
            return property_contact_tel;
        }

        public void  setPropertyContactTel (String property_contact_tel) {
            this.property_contact_tel = property_contact_tel;
        }

        public String getPropertyEmail() {
            return property_email;
        }

        public void setPropertyEmail( String property_email) {
            this.property_email = property_email;
        }

        public String getPropertyInitials() {
            return property_initials;
        }

        public void setPropertyInitials ( String property_initials) {
            this.property_initials = property_initials;
        }

        public String getPropertySurname () {
            return property_surname;
        }

        public void setPropertySurname (String property_surname) {
            this.property_surname = property_surname;
        }

        public String getPropertyPhysicalAddress () {
            return  property_physical_address;
        }

        public void setPropertyPhysicalAddress ( String property_physical_address) {
            this.property_physical_address = property_physical_address;
        }

    }
}