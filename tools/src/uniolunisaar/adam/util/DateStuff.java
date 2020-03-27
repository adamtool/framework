package uniolunisaar.adam.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A helping class for handling some date stuff.
 *
 * Provides a formatted string of the current date.
 *
 * @author Manuel Gieseking
 */
public class DateStuff {

    /**
     * Returns the current date in the form: "yyyy-MM-dd HH:mm:ss.SSS"
     *
     * @return - the formatted current date.
     */
    public static String getFormatedCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    /**
     * Returns the current date for appending it to the server messages in the
     * form: " - [yyyy-MM-dd HH:mm:ss.SSS]".
     *
     * @return - current date for appending to server messages.
     */
    public static String getFormatedCurrentTimeForAppending() {
        return "     - [" + getFormatedCurrentTime() + "]";
    }
}
