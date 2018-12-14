package me.mikasa.music.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.mikasa.music.R;
import me.mikasa.music.bean.MusicInfo;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.fragment.SingleMusicFragment;
import me.mikasa.music.receiver.PlayerManagerReceiver;
import me.mikasa.music.util.ChineseToEnglish;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;
import me.mikasa.music.view.ScanView;
import woo.mikasa.lib.base.BaseToolbarActivity;

public class ScanActivity extends BaseToolbarActivity {
    private DbManager manager;
    private ScanView scanView;
    private CheckBox filterCb;
    private Button scanBtn;
    private TextView scanCountTv;
    private Handler handler;
    private Message msg;
    private int progress = 0;
    private int musicCount = 0;
    private boolean scanning=false;
    private List<MusicInfo> musicInfoList;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initData() {
        mTitle.setText("扫描音乐");
        manager=DbManager.getInstance(mContext);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case Constant.SCAN_NO_MUSIC:
                        scanComplete();
                        break;
                    case Constant.SCAN_ERROR:
                        scanComplete();
                        break;
                    case Constant.SCAN_COMPLETE:
                        initCurrentPlaying();
                        scanComplete();
                        break;
                    case Constant.SCAN_UPDATE:
                        scanCountTv.setText("已扫描到" + progress + "首歌曲");
                        //scanPathTv.setText(path);
                        break;
                }
            }
        };
    }

    @Override
    protected void initView() {
        scanView =findViewById(R.id.scan_view);
        filterCb =findViewById(R.id.scan_filter_cb);
        scanCountTv =findViewById(R.id.scan_count);
        scanBtn=findViewById(R.id.scan_btn);
    }
    @Override
    protected void initListener() {
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!scanning) {
                    scanning = true;
                    scanView.start();
                    scanLocalMusic();
                    scanBtn.setText("停止扫描");
                } else {
                    scanning = false;
                    scanView.stop();
                    scanCountTv.setText("");
                    scanBtn.setText("开始扫描");
                }
            }
        });
    }
    private void initCurrentPlaying(){
        List<MusicInfo>list=manager.getAllMusic();
        MusicUtil.setShared(Constant.KEY_ID, list.get(0).getId());
    }
    private void scanLocalMusic(){
        //停止播放器
        Intent intent=new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        intent.putExtra(Constant.COMMAND,Constant.COMMAND_STOP);
        mContext.sendBroadcast(intent);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String[]musicInfoArray=new String[]{
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.DURATION,
                            MediaStore.Audio.Media.DATA
                    };
                    Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            musicInfoArray,null,null,null);
                    if (cursor!=null){//&&cursor.getCount()!=0
                        musicInfoList=new ArrayList<>();
                        while (cursor.moveToNext()){//moveToNext()
                            if (!scanning){
                                return;
                            }
                            String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                            String singer=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                            String album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                            String duration=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                            if (filterCb.isChecked()&&duration!=null&&Long.valueOf(duration)<1000*60){
                                continue;
                            }
                            File file=new File(path);
                            String parentPath = file.getParentFile().getPath();
                            name = replaceUnKnown(name);
                            singer = replaceUnKnown(singer);
                            album = replaceUnKnown(album);
                            path = replaceUnKnown(path);
                            MusicInfo musicInfo = new MusicInfo();
                            musicInfo.setName(name);
                            musicInfo.setSinger(singer);
                            musicInfo.setAlbum(album);
                            musicInfo.setPath(path);
                            musicInfo.setParentPath(parentPath);
                            musicInfo.setFirstLetter(ChineseToEnglish.StringToPinyinSpecial(name).toUpperCase().charAt(0)+"");
                            musicInfoList.add(musicInfo);
                            progress++;
                            musicCount = cursor.getCount();
                            msg = new Message();    //每次都必须new，必须发送新对象，不然会报错
                            msg.what = Constant.SCAN_UPDATE;//scan_update
                            msg.arg1 = musicCount;
                            handler.sendMessage(msg);
                            try {
                                sleep(60);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                        //扫描完成获取一下当前播放音乐及路径
                        //currentMusicId = MusicUtil.getIntShared(Constant.KEY_ID);
                        //currentMusicPath = manager.getMusicPath(currentMusicId);
                        // 根据a-z进行排序源数据
                        Collections.sort(musicInfoList);
                        manager.updateAllMusic(musicInfoList);
                        //扫描完成
                        msg = new Message();
                        msg.what = Constant.SCAN_COMPLETE;
                        handler.sendMessage(msg);  //更新UI界面
                    }else {
                        msg = new Message();
                        msg.what = Constant.SCAN_NO_MUSIC;
                        handler.sendMessage(msg);  //更新UI界面
                    }
                    if (cursor!=null){
                        cursor.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //扫描出错
                    msg = new Message();
                    msg.what = Constant.SCAN_ERROR;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }
    private void scanComplete(){
        scanView.stop();
        scanBtn.setText("扫描完成");
        scanning = false;
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!scanning){
                    ScanActivity.this.finish();
                }
            }
        });
        //通知UI更新
        Intent intent=new Intent();
        intent.setAction(SingleMusicFragment.UI_UPDATE);
        mContext.sendBroadcast(intent);
        //intent.setAction(HomeActivity.UI_UPDATE);
        //mContext.sendBroadcast(intent);
    }
    private static String replaceUnKnown(String oldStr){
        try {
            if (oldStr != null){
                if (oldStr.equals("<unknown>")){
                    oldStr = oldStr.replaceAll("<unknown>", "未知");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return oldStr;
    }

    @Override
    protected void onDestroy() {
        scanView.stop();
        super.onDestroy();
    }
}
