package me.mikasa.music.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import me.mikasa.music.service.MusicPlayerService;

/**
 * Created by mikasa on 2018/11/12.
 */
public class MusicApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        
        SPUtil.init(context);//初始化sharedPreferences
    }
    public static Context getContext(){
        return context;
    }
}
