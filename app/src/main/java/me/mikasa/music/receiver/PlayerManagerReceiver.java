package me.mikasa.music.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.os.Build;

import java.io.File;

import me.mikasa.music.R;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.fragment.PlayBarFragment;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;
import me.mikasa.music.util.NotificationUtil;
import me.mikasa.music.util.UpdateUIThread;

/**
 * Created by mikasa on 2018/11/12.
 * MusicPlayerService的BroadCastReceiver
 */
public class PlayerManagerReceiver extends BroadcastReceiver {
    public static final String ACTION_UPDATE=
            "me.mikasa.music.receiver.PlayerManagerReceiver.action_update";//广播接收地址,播放器UI更新
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private DbManager manager;
    private int threadNumber;
    private NotificationUtil notificationUtil;
    public static int status=Constant.STATUS_STOP;

    public PlayerManagerReceiver(){
    }
    public PlayerManagerReceiver(Context context){
        super();
        this.mContext=context;
        manager=DbManager.getInstance(context);
        notificationUtil=new NotificationUtil(context);
        mediaPlayer=new MediaPlayer();
        initMediaPlayer();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        int command = intent.getIntExtra(Constant.COMMAND,Constant.COMMAND_INIT);
        switch (command){
            case Constant.COMMAND_INIT:	//已经在创建的时候初始化了，可以撤销了
                break;
            case Constant.COMMAND_PLAY:
                status = Constant.STATUS_PLAY;
                String musicPath = intent.getStringExtra(Constant.KEY_PATH);
                if (musicPath!=null) {
                    playMusic(musicPath);//播放新的歌曲
                }else {
                    mediaPlayer.start();//继续播放之前暂停的歌曲
                }
                //notification
                showNotification(false);
                break;
            case Constant.COMMAND_PAUSE:
                mediaPlayer.pause();
                status = Constant.STATUS_PAUSE;
                showNotification(true);
                break;
            case Constant.COMMAND_STOP: //本程序停止状态都是删除当前播放音乐触发
                randomNumber();
                status = Constant.STATUS_STOP;
                if(mediaPlayer!=null) {
                    mediaPlayer.stop();
                }
                initStop();
                break;
            case Constant.COMMAND_PROGRESS://拖动进度
                int curProgress = intent.getIntExtra(Constant.KEY_CURRENT, 0);
                //异步的，可以设置完成监听来获取真正定位完成的时候
                mediaPlayer.seekTo(curProgress);
                break;
            case Constant.COMMAND_RELEASE:
                randomNumber();
                status = Constant.STATUS_STOP;
                if(mediaPlayer!=null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                break;
            case Constant.COMMAND_PLAY_NEXT:
                MusicUtil.playNextMusic(mContext);
                status = Constant.STATUS_PLAY;
                break;
            case Constant.COMMAND_PLAY_PRE:
                MusicUtil.playPreMusic(mContext);
                status = Constant.STATUS_PLAY;
                break;
        }
        updatePlayBarUI();
    }
    private void initMediaPlayer(){
        randomNumber();// 改变线程号,使旧的播放线程停止
        int musicId = MusicUtil.getIntShared(Constant.KEY_ID);
        int current = MusicUtil.getIntShared(Constant.KEY_CURRENT);
        // 如果是没取到当前正在播放的音乐ID，则从数据库中获取第一首音乐的播放信息初始化
        if (musicId == -1) {
            return;
        }
        String path = manager.getMusicPath(musicId);
        if (path == null) {
            return;
        }
        if (current == 0) {
            status = Constant.STATUS_STOP; // 设置播放状态为停止
        }else {
            status = Constant.STATUS_PAUSE; // 设置播放状态为暂停
        }
        MusicUtil.setShared(Constant.KEY_ID,musicId);
        MusicUtil.setShared(Constant.KEY_PATH,path);
        updatePlayBarUI();
    }
    private void initStop(){
        MusicUtil.setShared(Constant.KEY_ID,manager.getFirstId(Constant.ALLMUSIC));
    }
    private void playMusic(String path){
        randomNumber();
        if (mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                randomNumber();				//切换线程
                onComplete();               //调用音乐切换模块，进行相应操作
                updatePlayBarUI(); 				//更新界面
            }
        });
        try {
            File file=new File(path);
            if(!file.exists()){
                Toast.makeText(mContext,"歌曲文件不存在，请重新扫描",Toast.LENGTH_SHORT).show();
                MusicUtil.playNextMusic(mContext);
                return;
            }
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //开启playBarUI更新线程
            new UpdateUIThread(PlayerManagerReceiver.this,mContext,threadNumber).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updatePlayBarUI(){
        Intent playBarIntent = new Intent(PlayBarFragment.ACTION_UPDATE);    //接收广播为MusicUpdateMain
        playBarIntent.putExtra(Constant.STATUS, status);
        mContext.sendBroadcast(playBarIntent);
        Intent intent = new Intent(ACTION_UPDATE);    //接收广播为所有歌曲列表的adapter
        mContext.sendBroadcast(intent);
    }
    private void onComplete(){
        MusicUtil.playNextMusic(mContext);
    }
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }
    public int getThreadNumber(){
        return threadNumber;
    }
    //取一个（0，100）之间的不一样的随机数
    private void randomNumber(){
        int count;
        do {
            count =(int)(Math.random()*100);
        } while (count == threadNumber);
        threadNumber = count;
    }

    private void showNotification(boolean isPaused){
        RemoteViews remoteViews=getRemoteViews(isPaused);
        notificationUtil.getBuilder().setContent(remoteViews);
        notificationUtil.sendNotification();
    }

    //private static boolean isPaused=false;
    private RemoteViews getRemoteViews(boolean isPaused){
        PendingIntent playPauseIntent,nextIntent,preIntent;
        String musicName ,musicArtist;
        int musicId = MusicUtil.getIntShared(Constant.KEY_ID);
         RemoteViews remoteViews;
        if (Build.VERSION.SDK_INT>=26){
            remoteViews=new RemoteViews(mContext.getPackageName(),R.layout.layout_music_notification);
        }else {
            remoteViews=new RemoteViews(mContext.getPackageName(),R.layout.layout_music_notification_compat);
        }
        if (musicId == -1){
            remoteViews.setTextViewText(R.id.music_name,"音乐");
            remoteViews.setTextViewText(R.id.music_artist,"歌手");
        }else{
            musicName=manager.getMusicInfo(musicId).get(1);
            musicArtist=manager.getMusicInfo(musicId).get(2);
            remoteViews.setTextViewText(R.id.music_name,musicName);
            remoteViews.setTextViewText(R.id.music_artist,musicArtist);
        }
        Intent intent=new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        Intent next=new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        Intent pre=new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        if (isPaused){
            remoteViews.setImageViewResource(R.id.iv_play_pause,R.drawable.play_green);
            intent.putExtra(Constant.COMMAND,Constant.COMMAND_PLAY);
        }else {
            remoteViews.setImageViewResource(R.id.iv_play_pause,R.drawable.pause_green);
            intent.putExtra(Constant.COMMAND,Constant.COMMAND_PAUSE);
        }
        next.putExtra(Constant.COMMAND,Constant.COMMAND_PLAY_NEXT);
        pre.putExtra(Constant.COMMAND,Constant.COMMAND_PLAY_PRE);
        //requestCode
        playPauseIntent=PendingIntent.getBroadcast(mContext,0,
                intent,PendingIntent.FLAG_CANCEL_CURRENT);//FLAG_CANCEL_CURRENT??
        nextIntent=PendingIntent.getBroadcast(mContext,1,next,PendingIntent.FLAG_UPDATE_CURRENT);
        preIntent=PendingIntent.getBroadcast(mContext,2,pre,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause,playPauseIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_next,nextIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_pre,preIntent);
        return remoteViews;
    }
}
