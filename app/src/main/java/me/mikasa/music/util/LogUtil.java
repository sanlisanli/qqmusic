package me.mikasa.music.util;

import android.util.Log;

/**
 * Created by mikasa on 2018/11/12.
 */
public class LogUtil {
    private static final String TAG="mikasa";
    public static void v(String s){
        Log.v(TAG,s);
    }
    public static void d(String s){
        Log.d(TAG,s);
    }
    public static void i(String s){
        Log.i(TAG,s);
    }
    public static void w(String s){
        Log.w(TAG,s);
    }
    public static void e(String s){
        Log.e(TAG,s);
    }
}
