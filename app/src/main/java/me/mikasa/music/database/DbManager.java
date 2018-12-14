package me.mikasa.music.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.mikasa.music.bean.MusicInfo;
import me.mikasa.music.util.ChineseToEnglish;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;

import static me.mikasa.music.database.DbOpenHelper.ID_COLUMN;
import static me.mikasa.music.database.DbOpenHelper.MUSIC_ID_COLUMN;
/**
 * Created by mikasa on 2018/11/12.
 */
public class DbManager {
    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase database;
    private static DbManager instance=null;
    public DbManager(Context context){
        dbOpenHelper=new DbOpenHelper(context);
        database=dbOpenHelper.getWritableDatabase();
    }
    public static synchronized DbManager getInstance(Context context){
        if (instance==null){
            instance=new DbManager(context);
        }
        return instance;
    }
    public int getMusicCount(int table){
        int musicCount = 0;
        Cursor cursor = null;
        switch (table){
            case Constant.ALLMUSIC:
                cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, null, null, null, null, null);
                break;
            case Constant.MYLOVE:
                cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, DbOpenHelper.LOVE_COLUMN + " = ? ", new String[]{String.valueOf(1)}, null, null, null);
                break;
            case Constant.RECENTPLAY:
                cursor = database.query(DbOpenHelper.RECENT_PLAY_TABLE, null, null, null, null, null, null);
                break;
            case Constant.MYPLAY:
                cursor = database.query(DbOpenHelper.PLAY_LIST_TABLE, null, null, null, null, null, null);
                break;
        }
        if (cursor.moveToFirst()) {
            musicCount = cursor.getCount();
        }
        if (cursor != null) {
            cursor.close();
        }
        return musicCount;
    }
    /**
     *对数据库增删改查
     */
    public List<MusicInfo>getAllMusic(){
        List<MusicInfo> musicInfoList = new ArrayList<>();
        Cursor cursor = null;
        database.beginTransaction();
        try {
            cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, null, null, null, null, null);
            musicInfoList = parseMusicInfo(cursor);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            if (cursor!=null){
                cursor.close();
            }
        }
        return musicInfoList;
    }
    public List<MusicInfo>getAllMusic(int playList){
        List<Integer> idList = getMusicList(playList);
        List<MusicInfo> musicList = new ArrayList<>();
        for (int id : idList) {
            musicList.add(getSingleMusic(id));
        }
        return musicList;
    }
    public MusicInfo getSingleMusic(int id){
        List<MusicInfo> musicInfoList = null;
        MusicInfo musicInfo = null;
        Cursor cursor = null;
        database.beginTransaction();
        try {
            cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, ID_COLUMN + " = ?", new String[]{"" + id}, null, null, null);
            musicInfoList = parseMusicInfo(cursor);
            musicInfo = musicInfoList.get(0);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            if (cursor!=null){
                cursor.close();
            }
        }
        return musicInfo;
    }
    public String getMusicPath(int id){
        if (id == -1) {
            return null;
        }
        String path = null;
        Cursor cursor = null;
        setRecentPlay(id);        //每次播放一首新歌前都需要获取歌曲路径，所以可以在此设置最近播放
        try {
            cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, ID_COLUMN + " = ?", new String[]{"" + id}, null, null, null);
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(DbOpenHelper.PATH_COLUMN));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }
    public int getFirstId(int listNumber){
        Cursor cursor = null;
        int id = -1;
        try {
            switch (listNumber) {
                case Constant.ALLMUSIC:
                    cursor = database.rawQuery("select min(id) from " + DbOpenHelper.MUSIC_TABLE, null);
                    break;
                default:
                    break;
            }
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return id;
    }
    // 获取歌单列表
    public ArrayList<Integer>getMusicList(int playList){
        Cursor cursor = null;
        ArrayList<Integer> list = new ArrayList<Integer>();
        int musicId;
        switch (playList){
            case Constant.ALLMUSIC:
                cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, null, null, null, null, null);
                break;
            case Constant.MYLOVE:
                cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, DbOpenHelper.LOVE_COLUMN + " = ?", new String[]{"" + 1}, null, null, null);
                break;
            case Constant.RECENTPLAY:
                cursor = database.rawQuery("select * from "+DbOpenHelper.RECENT_PLAY_TABLE+" ORDER BY "+ DbOpenHelper.ID_COLUMN,null);
                break;
            case Constant.PLAYLIST:
                int listId = MusicUtil.getIntShared(Constant.KEY_LIST_ID);
                list = getMusicIdListByPlaylist(listId);
                break;
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                musicId = cursor.getInt(cursor.getColumnIndex("id"));
                list.add(musicId);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
    public ArrayList<Integer> getMusicIdListByPlaylist(int playlistId){
        Cursor cursor = null;
        database.beginTransaction();
        ArrayList<Integer> list = new ArrayList<>();
        try{
            String sql = "select * from "+DbOpenHelper.PLAY_LIST_MUSIC_TABLE+" where "+ ID_COLUMN+" = ? ";
            cursor = database.rawQuery(sql,new String[]{""+playlistId});
            while (cursor.moveToNext()) {
                int musicId = cursor.getInt(cursor.getColumnIndex(DbOpenHelper.MUSIC_ID_COLUMN));
                list.add(musicId);
            }
            database.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            database.endTransaction();
            if (cursor !=null)
                cursor.close();
        }
        return list;
    }
    public List<MusicInfo>getMusicListByPlayList(int playListId){
        List<MusicInfo> musicInfoList = new ArrayList<>();
        Cursor cursor = null;
        int id;
        database.beginTransaction();
        try{
            String sql = "select * from "+DbOpenHelper.PLAY_LIST_MUSIC_TABLE+" where "+ ID_COLUMN+" = ? ORDER BY "+ DbOpenHelper.ID_COLUMN;
            cursor = database.rawQuery(sql,new String[]{""+playListId});
            while (cursor.moveToNext()){
                MusicInfo musicInfo = new MusicInfo();
                id =  cursor.getInt(cursor.getColumnIndex(MUSIC_ID_COLUMN));
                musicInfo = getSingleMusic(id);
                musicInfoList.add(musicInfo);
            }
            database.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            database.endTransaction();
            if (cursor !=null)
                cursor.close();
        }
        return musicInfoList;
    }
    public boolean isMusicFavor(int id){
        Cursor cursor=null;
        String[] columns={"love"};//或者columns为null
        cursor=database.query(DbOpenHelper.MUSIC_TABLE,columns,ID_COLUMN + " = ?", new String[]{"" + id}, null, null, null);
        if (cursor.moveToFirst()){
            int love = cursor.getInt(cursor.getColumnIndex(DbOpenHelper.LOVE_COLUMN));
            if (love==1){
                if (cursor != null) {
                    cursor.close();
                }
                return true;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }
    // 获取歌曲详细信息
    public ArrayList<String> getMusicInfo(int id) {
        if (id == -1) {
            return null;
        }
        Cursor cursor = null;
        ArrayList<String> musicInfo = new ArrayList<String>();
        cursor = database.query(DbOpenHelper.MUSIC_TABLE, null, ID_COLUMN + " = ?", new String[]{"" + id}, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                musicInfo.add(i, cursor.getString(i));
            }
        } else {
            musicInfo.add("0");
            musicInfo.add("听听音乐");
            musicInfo.add("好音质");
            musicInfo.add("0");
            musicInfo.add("0");
            musicInfo.add("0");
            musicInfo.add("0");
            musicInfo.add("0");
        }
        if (cursor != null) {
            cursor.close();
        }
        return musicInfo;
    }

    private void setRecentPlay(int id){
        if (id == -1 || id == 0) {
            return;
        }
        ContentValues values = new ContentValues();
        ArrayList<Integer> lastList = new ArrayList<>();
        Cursor cursor = null;
        lastList.add(id);
        database.beginTransaction();
        try {
            cursor = database.rawQuery("select id from " + DbOpenHelper.RECENT_PLAY_TABLE, null);
            while (cursor.moveToNext()) {
                if (cursor.getInt(0) != id) {
                    lastList.add(cursor.getInt(0));
                }
            }
            database.delete(DbOpenHelper.RECENT_PLAY_TABLE, null, null);
            if (lastList.size() < 20) {
                for (int i = 0; i < lastList.size(); i++) {
                    values.put(ID_COLUMN, lastList.get(i));
                    database.insert(DbOpenHelper.RECENT_PLAY_TABLE, null, values);
                }
            } else {
                for (int i = 0; i < 20; i++) {
                    values.put(ID_COLUMN, lastList.get(i));
                    database.insert(DbOpenHelper.RECENT_PLAY_TABLE, null, values);
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public void setMyLove(int id){
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.LOVE_COLUMN, 1);//1，设置favor
        //update()
        database.update(DbOpenHelper.MUSIC_TABLE, values, ID_COLUMN + " = ? ", new String[]{"" + id});
    }
    public void removeMyLove(int id){
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.LOVE_COLUMN, 0);
        //update()
        database.update(DbOpenHelper.MUSIC_TABLE, values, ID_COLUMN + " = ? ", new String[]{"" + id});
    }
    public void updateAllMusic(List<MusicInfo> musicInfoList) {
        database.beginTransaction();
        try {
            deleteAllTable();
            insertMusicListToMusicTable(musicInfoList);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }
    private void deleteAllTable() {
        database.execSQL("PRAGMA foreign_keys=ON");
        database.delete(DbOpenHelper.MUSIC_TABLE, null, null);
        database.delete(DbOpenHelper.RECENT_PLAY_TABLE, null, null);
        database.delete(DbOpenHelper.PLAY_LIST_TABLE, null, null);
        database.delete(DbOpenHelper.PLAY_LIST_MUSIC_TABLE, null, null);
    }
    private List<MusicInfo>parseMusicInfo(Cursor cursor){
        List<MusicInfo> list = null;
        try {
            if (cursor != null) {
                list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
                    String name = cursor.getString(cursor.getColumnIndex(DbOpenHelper.NAME_COLUMN));
                    String singer = cursor.getString(cursor.getColumnIndex(DbOpenHelper.SINGER_COLUMN));
                    String album = cursor.getString(cursor.getColumnIndex(DbOpenHelper.ALBUM_COLUMN));
                    String duration = cursor.getString(cursor.getColumnIndex(DbOpenHelper.DURATION_COLUMN));
                    String path = cursor.getString(cursor.getColumnIndex(DbOpenHelper.PATH_COLUMN));
                    String parentPath = cursor.getString(cursor.getColumnIndex(DbOpenHelper.PARENT_PATH_COLUMN));
                    int love = cursor.getInt(cursor.getColumnIndex(DbOpenHelper.LOVE_COLUMN));
                    String firstLetter = cursor.getString(cursor.getColumnIndex(DbOpenHelper.FIRST_LETTER_COLUMN));
                    //javabean
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setSinger(singer);
                    musicInfo.setAlbum(album);
                    musicInfo.setPath(path);
                    musicInfo.setParentPath(parentPath);
                    musicInfo.setLove(love);
                    musicInfo.setDuration(duration);
                    musicInfo.setFirstLetter(firstLetter);
                    list.add(musicInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     *写入数据库
     */
    private void insertMusicListToMusicTable(List<MusicInfo> musicInfoList) {
        for (MusicInfo musicInfo : musicInfoList) {
            insertMusicInfoToMusicTable(musicInfo);
        }
    }
    //添加歌曲到音乐表
    private void insertMusicInfoToMusicTable(MusicInfo musicInfo) {
        ContentValues values;
        Cursor cursor = null;
        int id = 1;
        try {
            values = musicInfoToContentValues(musicInfo);
            String sql = "select max(id) from " + DbOpenHelper.MUSIC_TABLE + ";";
            cursor = database.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                //设置新添加的ID为最大ID+1
                id = cursor.getInt(0) + 1;
            }
            values.put(ID_COLUMN, id);
            database.insert(DbOpenHelper.MUSIC_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor!=null){
                cursor.close();
            }
        }
    }
    //把MusicInfo对象转为ContentValues对象
    private ContentValues musicInfoToContentValues(MusicInfo musicInfo) {
        ContentValues values = new ContentValues();
        try {
            values.put(DbOpenHelper.NAME_COLUMN, musicInfo.getName());
            values.put(DbOpenHelper.SINGER_COLUMN, musicInfo.getSinger());
            values.put(DbOpenHelper.ALBUM_COLUMN, musicInfo.getAlbum());
            values.put(DbOpenHelper.DURATION_COLUMN, musicInfo.getDuration());
            values.put(DbOpenHelper.PATH_COLUMN, musicInfo.getPath());
            values.put(DbOpenHelper.PARENT_PATH_COLUMN, musicInfo.getParentPath());
            values.put(DbOpenHelper.LOVE_COLUMN, musicInfo.getLove());
            values.put(DbOpenHelper.FIRST_LETTER_COLUMN, "" + ChineseToEnglish.StringToPinyinSpecial(musicInfo.getName()).toUpperCase().charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
}
