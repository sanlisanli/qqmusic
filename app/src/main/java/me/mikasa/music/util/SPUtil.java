package me.mikasa.music.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mikasacos on 2018/9/3.
 */

public class SPUtil {
    private static final String preName="music";
    private static SPUtil sInstance;
    private final SharedPreferences mPref;
    private SPUtil(Context context){
        mPref=context.getSharedPreferences(preName,Context.MODE_PRIVATE);
    }
    //初始化init
    public static synchronized void init(Context context){
        if (sInstance==null){
            sInstance=new SPUtil(context);
        }
    }
    //调用getInstance()
    public static synchronized SPUtil getInstance(){
        if (sInstance==null){
            throw new IllegalStateException("is not initialized");
        }
        return sInstance;
    }
    public void setBoolean(String key,boolean value){
        mPref.edit().putBoolean(key,value).commit();
    }
    public boolean getBoolean(String key,boolean value){
        return mPref.getBoolean(key, value);
    }
    public String getString(String key,String value){
        return mPref.getString(key, value);
    }
    public void setString(String key,String value){//edit(),commit()
        mPref.edit().putString(key, value).commit();
    }
    public int getInt(String key,int value){
        return mPref.getInt(key, value);
    }
    public void setInt(String key,int value){
        mPref.edit().putInt(key, value).commit();
    }
    public void remove(String key){
        mPref.edit().remove(key).commit();
    }
    public void clear(){
        mPref.edit().clear().commit();
    }
}
