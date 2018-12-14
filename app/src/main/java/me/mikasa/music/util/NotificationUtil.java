package me.mikasa.music.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import me.mikasa.music.R;

public class NotificationUtil {
    private static final String channel_id="channel_id";
    private static final String channel_name="music_service";
    private NotificationManager manager;
    private Context mContext;
    private static NotificationCompat.Builder builder;
    private static final int notification_id=1;

    public NotificationUtil(Context context){
        this.mContext=context;
        manager=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public  NotificationCompat.Builder getBuilder(){
        if (Build.VERSION.SDK_INT>=26){//适配android8.0
            builder=new NotificationCompat.Builder(mContext,channel_id);
            NotificationChannel channel=new NotificationChannel(channel_id,channel_name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);//添加通知渠道
        }else {
            builder=new NotificationCompat.Builder(mContext);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOngoing(true);
        return builder;
    }
    public void sendNotification(){
        manager.notify(notification_id,builder.build());
    }
}
