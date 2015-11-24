package app.defensivethinking.co.za.smartcitizen.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class SmartCitizenContract {

    public static final String CONTENT_AUTHORITY = "app.defensivethinking.co.za.smartcitizen";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USER = "user";
    public static final String PATH_PROPERTY = "property";
    public static final String PATH_METER_READING = "meter_reading";


    /* Inner class that defines the table contents of the user table */
    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        // Table name
        public static final String TABLE_NAME = "user";

        public static final String COLUMN_USER_ID      = "user_id";
        public static final String COLUMN_USER_EMAIL   = "user_email";
        public static final String COLUMN_USERNAME     = "username";
        public static final String COLUMN_UPDATED      = "updated";
        public static final String COLUMN_USER_HASH    = "user_hash";
        public static final String COLUMN_USER_SALT    = "user_salt";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PropertyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROPERTY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PROPERTY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PROPERTY;

        public static final String TABLE_NAME = "property";

        public static final String COLUMN_PROPERTY_ID                = "property_id";
        public static final String COLUMN_PROPERTY_PORTION           = "property_portion";
        public static final String COLUMN_PROPERTY_ACCOUNT_NUMBER    = "property_account_number";
        public static final String COLUMN_PROPERTY_BP                = "property_bp";
        public static final String COLUMN_PROPERTY_CONTACT_TEL       = "property_contact_tel";
        public static final String COLUMN_PROPERTY_EMAIL             = "property_email";
        public static final String COLUMN_PROPERTY_INITIALS          = "property_initials";
        public static final String COLUMN_PROPERTY_SURNAME           = "property_surname";
        public static final String COLUMN_PROPERTY_PHYSICAL_ADDRESS  = "property_physical_address";
        public static final String COLUMN_PROPERTY_OWNER             = "property_owner";
        public static final String COLUMN_PROPERTY_UPDATED           = "property_updated";


        public static Uri buildPropertyUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class MeterReading implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_METER_READING).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_METER_READING;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_METER_READING;

        public static final String TABLE_NAME = "meter_reading";

        public static final String COLUMN_METER_ID                      = "meter_id";
        public static final String COLUMN_METER_READING_DATE            = "meter_reading_date";
        public static final String COLUMN_METER_ELECTRICITY             = "meter_electricity";
        public static final String COLUMN_METER_WATER                   = "meter_water";
        public static final String COLUMN_METER_ACCOUNT_NUMBER          = "meter_account_number";

        public static Uri buildMeterUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

}
