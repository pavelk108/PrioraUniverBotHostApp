package ru.pavelkr.priorauniver;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/**
 * Created by pavel on 21.04.2017.
 */

public class AnonsProvider {
    public static final String APP_PREFERENCES = "my_settings";
    public static final String APP_PREFERENCES_TIME_ANONS = "time_anons";
    public static final String APP_PREFERENCES_MESS_ANONS = "mess_anons";


    public static void startAnons(Context context, String mes) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(APP_PREFERENCES_TIME_ANONS, System.currentTimeMillis());
        editor.putString(APP_PREFERENCES_MESS_ANONS, mes);
        editor.apply();
    }

    public static void resetAnons(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(APP_PREFERENCES_TIME_ANONS, 0);
        editor.apply();
    }

    public static long getTime(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.getLong(APP_PREFERENCES_TIME_ANONS, 0);
    }

    public static String getAnons(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.getString(APP_PREFERENCES_MESS_ANONS, "");
    }
}
