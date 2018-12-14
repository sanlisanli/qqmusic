package me.mikasa.music.bean;

/**
 * Created by mikasa on 2018/11/14.
 */
public class Music {
    private int imgId;
    private String title;
    private String artist;
    private String count;
    public void setImgId(int id){
        this.imgId=id;
    }
    public int getImgId(){
        return imgId;
    }
    public void setTitle(String s){
        this.title=s;
    }
    public String getTitle(){
        return title;
    }
    public void setArtist(String s){
        this.artist=s;
    }
    public String getArtist(){
        return artist;
    }
    public void setCount(String s){
        this.count=s;
    }
    public String getCount(){
        return count;
    }
}
