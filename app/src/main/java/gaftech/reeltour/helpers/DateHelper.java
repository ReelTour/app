package gaftech.reeltour.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by r.suleymanov on 18.07.2015.
 * email: ruslancer@gmail.com
 */

public class DateHelper {
    private static final String format = "yyyy-MM-dd HH:mm:ss";
    private static final String tz_id = "UTC";

    private SimpleDateFormat dateFormat = null;
    public DateHelper() {
        dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(tz_id));
    }
    public String dateToStr(Date date) {
        if (date == null) return null;
        return dateFormat.format(date);
    }
    public Date strToDate(String date) {
        if (date == null) return null;
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getCurrentDateFormatted() {
        return dateToStr(new Date());
    }
    public Date getCurrentDate() {
        return new Date();
    }

}
