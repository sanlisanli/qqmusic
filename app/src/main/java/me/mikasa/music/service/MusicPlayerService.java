package me.mikasa.music.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import me.mikasa.music.receiver.PlayerManagerReceiver;

/**
 * Created by mikasa on 2018/11/12.
 */
public class MusicPlayerService extends Service {
    private PlayerManagerReceiver mReceiver;
    private MusicBinder mBinder=new MusicBinder();
    public MusicPlayerService(){
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
         return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    public class MusicBinder extends Binder{
        public MusicBinder() {
            super();
        }
    }

    private void register(){
        mReceiver=new PlayerManagerReceiver(MusicPlayerService.this);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(PlayerManagerReceiver.ACTION_UPDATE);
        registerReceiver(mReceiver,intentFilter);//注册广播,接收广播信息
    }
    private void unRegister(){
        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }
}
