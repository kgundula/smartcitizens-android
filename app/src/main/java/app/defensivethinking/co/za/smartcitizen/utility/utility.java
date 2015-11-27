package app.defensivethinking.co.za.smartcitizen.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.CookieManager;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class utility {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_FORMAT_Y = "yyyy-MM-dd";
    public static final String ISO_DATE_FROM = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static CookieManager cookieManager;

    public static String base_url = "smartcitizen.diraulo.me";

    public static Context context;

    public utility(Context context) {
        utility.context = context;
    }

    public static String getDbDateString(Date date){
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        return dateFormatter.format(date);
    }

    public static String getDateFromString(Date myDate) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_Y);
        return dateFormatter.format(myDate);
    }

    public static String getDateFromISOFormat(String date) {
        DateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FROM);
        String current_date = "";
        try {
            Date c_date = dateFormat.parse(date);
            current_date = getDateFromString(c_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return current_date;
    }

    public static boolean isDeviceConnectedToInternet() {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
