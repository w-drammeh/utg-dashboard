package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class MDate {
    private static final SimpleDateFormat standardFormat = new SimpleDateFormat("dd/MM/yyyy H:m:s");
    public static final String VAL_SEPARATOR = "/";

    public static String now(){
        return standardFormat.format(new Date());
    }

    public static String formatDateOnly(Date d){
        return formatFully(d).split(" ")[0];
    }

    public static String formatFully(Date d){
        return standardFormat.format(d);
    }

    public static int thisYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int thisMonth(){
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static Date parse(String dateValue){
        try {
            return standardFormat.parse(dateValue);
        } catch (ParseException e) {
            App.silenceException(e);
            return null;
        }
    }

    /**
     * Gets the specified calendar property from the given date.
     */
    public static int getPropertyFrom(Date date, int value){
        final Calendar t = Calendar.getInstance();
        t.setTime(date);
        if (value == Calendar.MONTH) {
            return t.get(Calendar.MONTH) + 1;
        }
        return t.get(value);
    }

    public static String getMonthByName(int n){
        switch (n){
            case 1:{
                return "January";
            }
            case 2:{
                return "February";
            }
            case 3:{
                return "March";
            }
            case 4:{
                return "April";
            }
            case 5:{
                return "May";
            }
            case 6:{
                return "June";
            }
            case 7:{
                return "July";
            }
            case 8:{
                return "August";
            }
            case 9:{
                return "September";
            }
            case 10:{
                return "October";
            }
            case 11:{
                return "November";
            }
            case 12:{
                return "December";
            }

            default:{
                return null;
            }
        }
    }

    public static String getDayByName(int n){
        switch (n){
            case 1:{
                return "Monday";
            }
            case 2:{
                return "Tuesday";
            }
            case 3:{
                return "Wednesday";
            }
            case 4:{
                return "Thursday";
            }
            case 5:{
                return "Friday";
            }
            case 6:{
                return "Saturday";
            }
            case 7:{
                return "Sunday";
            }

            default:{
                return null;
            }
        }
    }

    /**
     * Gets a date by adding or subtracting the specified interval of days from the given date.
     */
    public static String daysAfter(Date date, int days) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return standardFormat.format(calendar.getTime());
    }

    /**
     * Returns true if the values presented by date1 and date2 fall in the same day.
     * This method / condition is rough, and time ignorant.
     */
    public static boolean sameDay(Date d1, Date d2) {
        return formatDateOnly(d1).equals(formatDateOnly(d2));
    }

    /**
     * Notice! It is converting the long to int, so this method is limited to int.
     */
    public static int actualDayDifference(Date d1, Date d2){
        return (int) ChronoUnit.DAYS.between(d1.toInstant(), d2.toInstant());
    }

    public static int getTimeValue(Date d){
        return  (getPropertyFrom(d, Calendar.HOUR) + 12) * Globals.HOUR_IN_MILLI +
                getPropertyFrom(d, Calendar.MINUTE) * Globals.MINUTE_IN_MILLI +
                getPropertyFrom(d, Calendar.SECOND) * Globals.SECOND_IN_MILLI;
    }

}
