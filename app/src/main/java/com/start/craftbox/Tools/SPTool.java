package com.start.craftbox.Tools;

import android.content.Context;
import android.content.SharedPreferences;

public class SPTool {
    private static final String Name = "CraftBox";

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Name, Context.MODE_PRIVATE);
    }

    public static void saveFloat(Context context,String name, float value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putFloat(name,value);
        editor.apply();
    }

    public static void saveInt(Context context,String name, int value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(name,value);
        editor.apply();
    }

    public static void saveString(Context context,String name, String value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(name,value);
        editor.apply();
    }

    public static void saveLong(Context context,String name, long value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(name,value);
        editor.apply();
    }

    public static void saveBoolean(Context context,String name, boolean value){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(name,value);
        editor.apply();
    }

    public static float getFloat(Context context,String name ,float defaultvalue){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getFloat(name,defaultvalue);
    }

    public static int getInt(Context context,String name ,int defaultvalue){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getInt(name,defaultvalue);
    }

    public static float getLong(Context context,String name ,Long defaultvalue){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getLong(name,defaultvalue);
    }

    public static String getString(Context context,String name ,String defaultvalue){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(name,defaultvalue);
    }

    public static boolean getBoolean(Context context,String name ,boolean defaultvalue){
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getBoolean(name,defaultvalue);
    }

    public static void deleteData(Context context , String name){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(name);
        editor.apply();
    }

}
