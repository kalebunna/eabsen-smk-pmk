package com.example.eabsen.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class sessionManager {
    private static final String SESSION_NAME = "NamaSesi";

    // Metode untuk menyimpan string ke sesi
    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static void saveBoolean(Context context, String key, Boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }




    // Metode untuk mengambil string dari sesi dengan nilai default
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static Boolean getBoolean(Context context, String key, Boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    // Metode untuk menghapus data dari sesi
    public static void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
