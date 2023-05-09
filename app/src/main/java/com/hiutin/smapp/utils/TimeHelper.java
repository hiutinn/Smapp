package com.hiutin.smapp.utils;

import java.util.Date;

public class TimeHelper{
    public static String getTime(Date timestamp) {
        String time;
        // Convert the timestamp to milliseconds
        long timestampInMillis = timestamp.getTime();

        // Get the current time in milliseconds
        long currentInMillis = System.currentTimeMillis();

        // Calculate the difference in milliseconds
        long differenceInMillis = currentInMillis - timestampInMillis;

        // Convert the difference to days, hours, minutes, and seconds
        long seconds = differenceInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        if (days > 0) {
            time = days + "d";
        } else if (hours > 0) {
            time = hours % 24 + "h";
        } else if (minutes >= 1) {
            time = minutes % 60 + "m";
        } else {
            time = "A few seconds ago";
        }
        return time;
    }
}
