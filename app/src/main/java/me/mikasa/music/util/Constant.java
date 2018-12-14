package me.mikasa.music.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.mikasa.music.R;
import me.mikasa.music.bean.Music;

/**
 * Created by mikasa on 2018/11/12.
 */
public class Constant {
    public static final String COMMAND = "command";
    public static final int COMMAND_INIT = 1; //初始化命令
    public static final int COMMAND_PLAY = 2; //播放命令
    public static final int COMMAND_PAUSE = 3; //暂停命令
    public static final int COMMAND_STOP = 4; //停止命令
    public static final int COMMAND_PROGRESS = 5; //改变进度命令
    public static final int COMMAND_RELEASE = 6; //退出程序时释放
    public static final int COMMAND_PLAY_NEXT = 7; //播放命令
    public static final int COMMAND_PLAY_PRE = 8; //播放命令
    //播放状态
    public static final String STATUS = "status";
    public static final int STATUS_STOP = 0; //停止状态
    public static final int STATUS_PLAY = 1; //播放状态
    public static final int STATUS_PAUSE = 2; //暂停状态
    public static final int STATUS_RUN = 3;  //   状态
    //播放模式
    public static final int PLAYMODE_SEQUENCE = -1;
    public static final int PLAYMODE_SINGLE_REPEAT = 1;
    public static final int PLAYMODE_RANDOM = 2;
    //表单
    public static final int ALLMUSIC=0;
    public static final int RECENTPLAY=1;
    public static final int MYLOVE=2;
    public static final int MYPLAY=3;
    public static final int PLAYLIST=4;
    //SharedPreferences key 常量
    public static final String KEY_ID = "id";
    public static final String KEY_PATH = "path";
    public static final String KEY_MODE = "mode";
    public static final String KEY_LIST = "list";
    public static final String KEY_LIST_ID = "list_id";
    public static final String KEY_CURRENT = "current";
    public static final String KEY_DURATION = "duration";
    //handle常量
    public static final int SCAN_ERROR = 0;
    public static final int SCAN_COMPLETE = 1;
    public static final int SCAN_UPDATE = 2;
    public static final int SCAN_NO_MUSIC = 3;
    //MediaPlayerManager.action
    public static final String MP_FILTER = "me.mikasa.music.start_mediaplayer";
    public static List<Integer>getWelcomeImgs(){
        Integer[] ids={R.drawable.wel01,R.drawable.wel02,R.drawable.wel03,R.drawable.wel04};
        List<Integer>imgs=new ArrayList<>(ids.length);
        Collections.addAll(imgs,ids);
        return imgs;
    }
    public static List<Integer>getBannerImgs(){
        Integer[] imgIds={R.drawable.mv01,R.drawable.mv02,R.drawable.mv03,
                R.drawable.mv04,R.drawable.mv05,R.drawable.mv06};
        List<Integer>imgs=new ArrayList<>(imgIds.length);//array.length
        Collections.addAll(imgs,imgIds);//Collections.addAll(list,array);
        return imgs;
    }
    public static List<Music>getMv(){
        List<Music>musicList=new ArrayList<>(6);
        String[] titles={"One More Time","Consequences","默默","绑梦","Bazzaya","Perfume"};
        String[] artists={"Super Junior","Camila Cabello","A-Lin","容祖儿","채연","张杰"};
        Integer[] imgIds={R.drawable.mv01,R.drawable.mv02,R.drawable.mv03,
                          R.drawable.mv04,R.drawable.mv05,R.drawable.mv06};
        for (int i=0;i<6;i++){
            Music music=new Music();
            music.setImgId(imgIds[i]);
            music.setTitle(titles[i]);
            music.setArtist(artists[i]);
            musicList.add(music);
        }
        return musicList;
    }
}
