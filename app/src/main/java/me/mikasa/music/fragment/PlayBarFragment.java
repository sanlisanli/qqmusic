package me.mikasa.music.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import me.mikasa.music.R;
import me.mikasa.music.activity.PlayActivity;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.receiver.PlayerManagerReceiver;
import me.mikasa.music.service.MusicPlayerService;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;
import me.mikasa.music.util.PopupMenuUtil;
import me.mikasa.music.view.PlayingPopWindow;
import woo.mikasa.lib.base.BaseFragment;
import static me.mikasa.music.receiver.PlayerManagerReceiver.status;

/**
 * Created by mikasa on 2018/11/12.
 */
public class PlayBarFragment extends BaseFragment implements View.OnClickListener {
    public static final String ACTION_UPDATE =
            "me.mikasa.music.fragment.PlayBarFragment.action_update";
    private LinearLayout playBarLl;
    private ImageView playIv;
    private SeekBar seekBar;
    private ImageView nextIv;
    private ImageView menuIv;
    private TextView musicNameTv;
    private TextView singerNameTv;
    private PlayBarReceiver mReceiver;
    private DbManager manager;
    private Context mContext;

    public static synchronized PlayBarFragment newInstance(){
        return new PlayBarFragment();
    }
    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_play_bar;
    }

    @Override
    protected void initData(Bundle bundle) {
        mContext=mBaseActivity;//mBaseActivity为baseFragment依附的baseActivity
        manager=DbManager.getInstance(mContext);
        register();
    }

    @Override
    protected void initView() {
        findView();
        setMusicInfo();
        initPlayIv();
    }
    private void findView(){
        View view=mRootView;
        playBarLl = view.findViewById(R.id.home_activity_playbar_ll);
        seekBar = view.findViewById(R.id.home_seekbar);
        playIv = view.findViewById(R.id.play_iv);
        menuIv = view.findViewById(R.id.play_menu_iv);
        nextIv = view.findViewById(R.id.next_iv);
        musicNameTv = view.findViewById(R.id.home_music_name_tv);
        singerNameTv = view.findViewById(R.id.home_singer_name_tv);
    }

    @Override
    protected void setListener() {
        playBarLl.setOnClickListener(this);
        playIv.setOnClickListener(this);
        nextIv.setOnClickListener(this);
        menuIv.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    private void setMusicInfo(){
        int musicId = MusicUtil.getIntShared(Constant.KEY_ID);
        if (musicId == -1){
            musicNameTv.setText("音乐");
            singerNameTv.setText("歌手");
        }else{
            musicNameTv.setText(manager.getMusicInfo(musicId).get(1));
            singerNameTv.setText(manager.getMusicInfo(musicId).get(2));
        }
    }
    private void initPlayIv(){
       // int status = PlayerManagerReceiver.status;
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
    private void register(){
        mReceiver=new PlayBarReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE);
        mContext.registerReceiver(mReceiver,intentFilter);
    }
    private void unRegister(){
        if (mReceiver!=null){
            mContext.unregisterReceiver(mReceiver);
        }
    }
     @Override
    public void onResume() {
        super.onResume();
      
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_activity_playbar_ll:
                Intent intent=new Intent(mContext,PlayActivity.class);
                startActivity(intent);
                break;
            case R.id.play_iv:
                playMusic();
                break;
            case R.id.next_iv:
                playNext();
                break;
            case R.id.play_menu_iv:
                showPopFormBottom();
                break;
        }
    }
    private void playMusic(){
        int musicId = MusicUtil.getIntShared(Constant.KEY_ID);
        if (musicId == -1 || musicId == 0) {
            Intent intent = new Intent(Constant.MP_FILTER);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
            mContext.sendBroadcast(intent);
            Toast.makeText(getActivity(), "请先扫描本地歌曲",Toast.LENGTH_SHORT).show();
            return;
        }
        //如果当前媒体在播放音乐状态，则图片显示暂停图片，按下播放键，则发送暂停媒体命令，图片显示播放图片。以此类推。
        if (status == Constant.STATUS_PAUSE) {
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND,Constant.COMMAND_PLAY);
            mContext.sendBroadcast(intent);
        }else if (status == Constant.STATUS_PLAY) {
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PAUSE);
            mContext.sendBroadcast(intent);
        }else {
            //为停止状态时发送播放命令，并发送将要播放歌曲的路径
            String path = manager.getMusicPath(musicId);
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
            intent.putExtra(Constant.KEY_PATH, path);
            mContext.sendBroadcast(intent);
        }
    }
    private void playNext(){
        MusicUtil.playNextMusic(mContext);
    }

    private void showPopFormBottom() {
        PopupMenuUtil.showPopupMenu(mBaseActivity,mRootView);
    }

    class PlayBarReceiver extends BroadcastReceiver{
        int status;
        int duration;
        int current;
        @Override
        public void onReceive(Context context, Intent intent) {
            setMusicInfo();
            status = intent.getIntExtra(Constant.STATUS,0);
            current = intent.getIntExtra(Constant.KEY_CURRENT,0);
            duration = intent.getIntExtra(Constant.KEY_DURATION,100);
            switch (status){
                case Constant.STATUS_STOP:
                    playIv.setSelected(false);
                    seekBar.setProgress(0);
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
