package app.defensivethinking.co.za.smartcitizen.utility;

import java.net.CookieManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Profusion on 2015-03-17.
 */
public class utility {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static CookieManager cookieManager;


    public static String getDbDateString(Date date){
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        return dateFormatter.format(date);
    }

    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }
}
