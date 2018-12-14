package me.mikasa.music.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.mikasa.music.bean.MusicInfo;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.receiver.PlayerManagerReceiver;

/**
 * Created by mikasa on 2018/11/12.
 */
public class MusicUtil {
    public static List<MusicInfo>getCurrentPlaylist(Context context){
        DbManager manager=DbManager.getInstance(context);
        int playList=MusicUtil.getIntShared(Constant.KEY_LIST);
        List<MusicInfo> musicInfoList = new ArrayList<>();
        switch (playList){
            case Constant.ALLMUSIC:
                musicInfoList=manager.getAllMusic();
                break;
            case Constant.MYLOVE:
                musicInfoList=manager.getAllMusic(Constant.MYLOVE);
                break;
            case Constant.RECENTPLAY:
                musicInfoList=manager.getAllMusic(Constant.RECENTPLAY);
                break;
            case Constant.PLAYLIST:
                int listId=MusicUtil.getIntShared(Constant.KEY_LIST_ID);
                musicInfoList=manager.getMusicListByPlayList(listId);
                break;
        }
        return musicInfoList;
    }
    public static void playNextMusic(Context context){
        DbManager manager=DbManager.getInstance(context);
        int playMode=MusicUtil.getIntShared(Constant.KEY_MODE);
        int musicId=MusicUtil.getIntShared(Constant.KEY_ID);
        List<MusicInfo>musicList=getCurrentPlaylist(context);
        ArrayList<Integer> musicIdList =new ArrayList<>();
        for (MusicInfo info : musicList){
            musicIdList.add(info.getId());
        }
        musicId = getNextMusicId(musicIdList,musicId,playMode);
        MusicUtil.setShared(Constant.KEY_ID,musicId);
        if (musicId == -1) {
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);//stop
            context.sendBroadcast(intent);
            Toast.makeText(context, "歌曲不存在",Toast.LENGTH_LONG).show();
            return;
        }
        //获取播放歌曲路径
        String path = manager.getMusicPath(musicId);
        //发送播放请求
        Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);//play
        intent.putExtra(Constant.KEY_PATH, path);//path
        context.sendBroadcast(intent);
    }
    public static void playPreMusic(Context context){
        DbManager manager = DbManager.getInstance(context);
        int playMode = MusicUtil.getIntShared(Constant.KEY_MODE);
        int musicId = MusicUtil.getIntShared(Constant.KEY_ID);
        List<MusicInfo> musicList = getCurrentPlaylist(context);
        ArrayList<Integer> musicIdList =new ArrayList<>();
        for (MusicInfo info : musicList){
            musicIdList.add(info.getId());
        }
        musicId = getPreMusicId(musicIdList,musicId,playMode);
        MusicUtil.setShared(Constant.KEY_ID,musicId);
        if (musicId == -1) {
            Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
            intent.putExtra(Constant.COMMAND, Constant.COMMAND_STOP);
            context.sendBroadcast(intent);
            Toast.makeText(context, "歌曲不存在",Toast.LENGTH_LONG).show();
            return;
        }
        //获取播放歌曲路径
        String path = manager.getMusicPath(musicId);
        //发送播放请求
        Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
        intent.putExtra(Constant.KEY_PATH, path);
        context.sendBroadcast(intent);
    }
    public static void setShared(String key,int value){
        SPUtil.getInstance().setInt(key, value);
    }
    public static void setShared(String key,String value){
        SPUtil.getInstance().setString(key, value);
    }
    public static int getIntShared(String key){
        if (key.equals(Constant.KEY_CURRENT)){
            return SPUtil.getInstance().getInt(key,0);
        }else {
            return SPUtil.getInstance().getInt(key,-1);
        }
    }
    public static String getStringShared(String key){
        return SPUtil.getInstance().getString(key,null);
    }
    private static int getNextMusicId(ArrayList<Integer>musicList,int id,int playMode){
        if (id == -1) {
            return -1;
        }
        //找到当前id在列表的第几个位置（i+1）
        int index = musicList.indexOf(id);
        if (index == -1) {
            return -1;
        }
        switch (playMode){
            case Constant.PLAYMODE_SEQUENCE:
                if ((index + 1) == musicList.size()) {
                    id = musicList.get(0);
                } else {
                    ++index;//++index
                    id = musicList.get(index);
                }
                break;
            case Constant.PLAYMODE_SINGLE_REPEAT:
                break;
            case Constant.PLAYMODE_RANDOM:
                id = getRandomMusicId(musicList, id);
                break;
        }
        return id;
    }
    private static int getPreMusicId(ArrayList<Integer>musicList,int id,int playMode){
        if (id == -1) {
            return -1;
        }
        //找到当前id在列表的第几个位置（i+1）
        int index = musicList.indexOf(id);
        if (index == -1) {
            return -1;
        }
        switch (playMode){
            case Constant.PLAYMODE_SEQUENCE:
                if (index == 0) {
                    id = musicList.get(musicList.size() - 1);
                } else {
                    --index;
                    id = musicList.get(index);
                }
                break;
            case Constant.PLAYMODE_SINGLE_REPEAT:
                break;
            case Constant.PLAYMODE_RANDOM:
                id = getRandomMusicId(musicList, id);
                break;
        }
        return id;
    }
    private static int getRandomMusicId(ArrayList<Integer> list, int id) {
        int musicId;
        if (id == -1) {
            return -1;
        }
        if (list.isEmpty()) {
            return -1;
        }
        if (list.size() == 1) {
            return id;
        }
        do {
            int count = (int) (Math.random() * list.size());
            musicId = list.get(count);
        } while (musicId == id);
        return musicId;
    }
}
