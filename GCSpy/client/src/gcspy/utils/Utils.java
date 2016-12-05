/**
 * * $Id: Utils.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.utils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * General utilities
 * @author Tony Printezis
 */
public class Utils {

    /** ****************** Formatting ******************* */

    static private NumberFormat percentFormatter;
    static private NumberFormat longSizeFormatter;
    static private NumberFormat doubleSizeFormatter;

    static {
        percentFormatter = NumberFormat.getInstance();
        percentFormatter.setMinimumFractionDigits(1);
        percentFormatter.setMaximumFractionDigits(1);

        doubleSizeFormatter = NumberFormat.getInstance();
        doubleSizeFormatter.setMinimumFractionDigits(2);
        doubleSizeFormatter.setMaximumFractionDigits(2);

        longSizeFormatter = NumberFormat.getInstance();
    }

    /**
     * Format as a percentage
     * @param perc the value to format
     * @return the value as a percentage
     */
    static public String formatPercentage(double perc) {
        return percentFormatter.format(perc);
    }

    /**
     * Format as a size
     * @param size the value to format
     * @return the formatted value
     */
    static public String formatSize(long size) {
        return longSizeFormatter.format(size);
    }

    /**
     * Format as a size
     * @param size the value to format
     * @return the formatted value
     */
    static public String formatSize(double size) {
        return doubleSizeFormatter.format(size);
    }

    /** ****************** Date/Time Formatting ******************* */

    static private final String DAYS_OF_WEEK[] = {null, // Sunday is 1
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    static private final String MONTHS[] = {"Jan", "Feb", "Mar", "Apr", "May",
            "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    static private DateFormat timeFormatter;

    static {
        timeFormatter = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    }

    /**
     * Format as a date
     * @param date The date
     * @return the formatted date
     */
    static public String formatDate(Date date) {
        // return dateFormatter.format(date);
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int month = cal.get(Calendar.MONTH);
        String day = padLeft("" + cal.get(Calendar.DAY_OF_MONTH), 2);
        int year = cal.get(Calendar.YEAR);
        return DAYS_OF_WEEK[dayOfWeek] + " " + MONTHS[month] + " " + day + ", " + year;
    }

    /**
     * Format as a time
     * @param date the time to format
     * @return the formatted time
     */
    static public String formatTime(Date date) {
        return timeFormatter.format(date);
    }

    /**
     * Format as a time and date
     * @param date the date to format
     * @return the formatted time and date
     */
    static public String formatDateTime(Date date) {
        return formatTime(date) + ", " + formatDate(date);
    }

    /** ****************** Padding ******************* */

    static private String getPadding(int len) {
        String str = "";
        for (int i = 0; i < len; ++i)
            str += " ";
        return str;
    }

    /**
     * Pad a string to the left
     * @param str the string
     * @param len the space in which to place the string
     * @return the padded string
     */
    static public String padLeft(String str, int len) {
        int strLen = str.length();
        if (strLen < len)
            return getPadding(len - strLen) + str;
        else
            return str;
    }

    /**
     * Pad a string to the right
     * @param str the string
     * @param len the space in which to place the string
     * @return the padded string
     */
    static public String padRight(String str, int len) {
        int strLen = str.length();
        if (strLen < len)
            return str + getPadding(len - strLen);
        else
            return str;
    }

    /** ****************** String Utils ******************* */

    /**
     * Count the number of occurrences of a character in a string
     * @param str the string
     * @param c the character
     * @return the number of occurrences
     */
    static public int countChars(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == c)
                ++count;
        }
        return count;
    }

    /**
     * Count the number of lines in a string
     * @param text The test
     * @return the count
     */
    static public int countLines(String text) {
        if (text == null)
            return 0;

        int count = 1;
        char arr[] = text.toCharArray();
        for (char anArr : arr) {
            if (anArr == '\n')
                ++count;
        }

        return count;
    }

    /** ****************** Threads ******************* */

    /**
     * Sleep for a period
     * @param millis the period to sleep in milliseconds
     */
    static public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

}
