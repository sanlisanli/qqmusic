package me.mikasa.music.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import me.mikasa.music.R;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.fragment.PlayBarFragment;
import me.mikasa.music.receiver.PlayerManagerReceiver;
import me.mikasa.music.service.MusicPlayerService;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;
import me.mikasa.music.util.PopupMenuUtil;
import woo.mikasa.lib.base.BaseActivity;

public class PlayActivity extends BaseActivity implements View.OnClickListener {
    private DbManager manager;
    private ImageView backIv,playIv, menuIv, preIv,nextIv,modeIv,musicFavor;
    private TextView curTimeTv,totalTimeTv,musicNameTv,musicArtistTv;
    private SeekBar seekBar;
    private PlayReceiver mReceiver;
    private int mProgress,duration,current;
    private ServiceConnection musicConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void createContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(setLayoutResId());
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initData() {
        manager=DbManager.getInstance(mContext);
        register();
        Intent intent=new Intent(PlayActivity.this,MusicPlayerService.class);
        bindService(intent,musicConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void initView() {
        findView();
        initPlayMode();
        initMusicInfo();
        initPlayIv();
    }
    private void findView(){
        musicFavor=findViewById(R.id.iv_music_favor);
        backIv =findViewById(R.id.iv_back);
        playIv =findViewById(R.id.iv_play);
        menuIv =findViewById(R.id.iv_menu);
        preIv =findViewById(R.id.iv_prev);
        nextIv =findViewById(R.id.iv_next);
        modeIv =findViewById(R.id.iv_mode);
        curTimeTv =findViewById(R.id.tv_current_time);
        totalTimeTv =findViewById(R.id.tv_total_time);
        musicNameTv =findViewById(R.id.tv_title);
        musicArtistTv =findViewById(R.id.tv_artist);
        seekBar =findViewById(R.id.activity_play_seekbar);
    }

    @Override
    protected void initListener() {
        musicFavor.setOnClickListener(this);
        backIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        menuIv.setOnClickListener(this);
        preIv.setOnClickListener(this);
        nextIv.setOnClickListener(this);
        modeIv.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress;
                initTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               seekPlay();
            }
        });
    }
    private void seekPlay(){
        int musicId=MusicUtil.getIntShared(Constant.KEY_ID);
        if (musicId==-1){
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra("cmd", Constant.COMMAND_STOP);
            sendBroadcast(intent);
            Toast.makeText(PlayActivity.this, "歌曲不存在", Toast.LENGTH_LONG).show();
            return;
        }
        //发送播放请求
        Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        intent.putExtra(Constant.COMMAND, Constant.COMMAND_PROGRESS);
        intent.putExtra(Constant.KEY_CURRENT, mProgress);
        sendBroadcast(intent);
    }
    private void playMusic(){
        int musicId;
        musicId = MusicUtil.getIntShared(Constant.KEY_ID);
        //如果当前媒体在播放音乐状态，则图片显示暂停图片，按下播放键，则发送暂停媒体命令，图片显示播放图片。以此类推。
        if (PlayerManagerReceiver.status == Constant.STATUS_PAUSE) {
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
            sendBroadcast(intent);
        } else if (PlayerManagerReceiver.status == Constant.STATUS_PLAY) {
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PAUSE);
            sendBroadcast(intent);
        } else {
            //为停止状态时发送播放命令，并发送将要播放歌曲的路径
            String path = manager.getMusicPath(musicId);
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
            intent.putExtra(Constant.KEY_PATH, path);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_mode:
                switchPlayMode();
                break;
            case R.id.iv_play:
                playMusic();
                break;
            case R.id.iv_next:
                MusicUtil.playNextMusic(this);
                break;
            case R.id.iv_prev:
                MusicUtil.playPreMusic(this);
                break;
            case R.id.iv_menu:
                showPopFormBottom();
                break;
            case R.id.iv_music_favor:
                setOrRemoveFavor();
                break;
        }
    }


    private void setOrRemoveFavor(){
        int musicId = MusicUtil.getIntShared(Constant.KEY_ID);
        if (manager.isMusicFavor(musicId)){
            manager.removeMyLove(musicId);
            musicFavor.setImageResource(R.drawable.love_white);
        }else {
            manager.setMyLove(musicId);
            musicFavor.setImageResource(R.drawable.love_red);
        }
    }
    private void showPopFormBottom(){
        PopupMenuUtil.showPopupMenu(this,findViewById(R.id.activity_play));
    }
    private void initPlayIv(){
        int status = PlayerManagerReceiver.status;
        switch (status) {
            case Constant.STATUS_STOP:
                playIv.setSelected(false);
                break;
            case Constant.STATUS_PLAY:
                playIv.setSelected(true);
                break;
            case Constant.STATUS_PAUSE:
                playIv.setSelected(false);
                break;
            case Constant.STATUS_RUN:
                playIv.setSelected(true);
                break;
        }
    }
    private void initPlayMode() {
        int playMode = MusicUtil.getIntShared(Constant.KEY_MODE);
        if (playMode == -1) {
            playMode = 0;
        }
        modeIv.setImageLevel(playMode);
    }
    private void initMusicInfo() {
        int musicId = MusicUtil.getIntShared(Constant.KEY_ID);
        if (musicId == -1) {
            musicNameTv.setText("音乐");
            musicArtistTv.setText("歌手");
        } else {
            musicNameTv.setText(manager.getMusicInfo(musicId).get(1));
            musicArtistTv.setText(manager.getMusicInfo(musicId).get(2));
            if (manager.isMusicFavor(musicId)){
                musicFavor.setImageResource(R.drawable.love_red);
            }else {
                musicFavor.setImageResource(R.drawable.love_white);
            }
        }
    }
    private void initTime() {
        curTimeTv.setText(formatTime(current));
        totalTimeTv.setText(formatTime(duration));
    }
    private void switchPlayMode() {
        int playMode = MusicUtil.getIntShared(Constant.KEY_MODE);
        switch (playMode) {
            case Constant.PLAYMODE_SEQUENCE:
                MusicUtil.setShared(Constant.KEY_MODE, Constant.PLAYMODE_RANDOM);
                break;
            case Constant.PLAYMODE_RANDOM:
                MusicUtil.setShared(Constant.KEY_MODE, Constant.PLAYMODE_SINGLE_REPEAT);
                break;
            case Constant.PLAYMODE_SINGLE_REPEAT:
                MusicUtil.setShared(Constant.KEY_MODE, Constant.PLAYMODE_SEQUENCE);
                break;
        }
        initPlayMode();
    }
    private void register() {
        mReceiver = new PlayReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayBarFragment.ACTION_UPDATE);
        registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
    private String formatTime(long time) {
        return formatTime("mm:ss", time);
    }
    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
        unbindService(musicConnection);
    }

    class PlayReceiver extends BroadcastReceiver{
        int status;
        @Override
        public void onReceive(Context context, Intent intent) {
            initMusicInfo();
            status = intent.getIntExtra(Constant.STATUS, 0);
            current = intent.getIntExtra(Constant.KEY_CURRENT, 0);
            duration = intent.getIntExtra(Constant.KEY_DURATION, 100);
            switch (status) {
                case Constant.STATUS_STOP:
                    playIv.setSelected(false);
                    break;
                case Constant.STATUS_PLAY:
                    playIv.setSelected(true);
                    break;
                case Constant.STATUS_PAUSE:
                    playIv.setSelected(false);
                    break;
                case Constant.STATUS_RUN:
                    playIv.setSelected(true);
                    seekBar.setMax(duration);
                    seekBar.setProgress(current);
                    break;
                default:
                    break;
            }
        }
    }
}
