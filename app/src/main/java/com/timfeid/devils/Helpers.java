package com.timfeid.devils;

import android.util.Log;

import java.util.Locale;

/**
 * Created by Tim on 2/13/2018.
 * Helpers for the app
 */

class Helpers {
    static void d(String msg) {
        Log.d("HELPER", msg);
    }

    static int stringTimeToSeconds(String time) {
        int index = time.indexOf(':');

        if (index == -1) {
            return 0;
        }

        int totalSecs = Integer.parseInt(time.substring(0, index)) * 60;

        return totalSecs + Integer.parseInt(time.substring(index +1, time.length()));
    }

    static String secondsToString(int totalSecs) {
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    static String ordinal(int num) {
        if (num >= 11 && num <= 13) {
            return num+"th";
        }

        switch (num % 10) {
            case 1:
                return num+"st";
            case 2:
                return num+"nd";
            case 3:
                return num+"rd";
        }

        return num+"th";
    }
}
