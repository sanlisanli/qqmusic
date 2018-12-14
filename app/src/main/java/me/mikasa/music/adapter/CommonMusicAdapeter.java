package me.mikasa.music.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.mikasa.music.R;
import me.mikasa.music.bean.MusicInfo;
import woo.mikasa.lib.base.BaseRvAdapter;

/**
 * Created by mikasa on 2018/11/15.
 */
public class CommonMusicAdapeter extends BaseRvAdapter<MusicInfo> {
    private Context mContext;
    public CommonMusicAdapeter(Context context){
        this.mContext=context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.layout_common_music,parent,false);
        return new CommonMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CommonMusicHolder)holder).bindView(mDataList.get(position));
    }
    class CommonMusicHolder extends BaseRvViewHolder{
        LinearLayout commonMusicLl;
        TextView musicIndex, musicName,musicSinger;
        ImageView menuIv;
        CommonMusicHolder(View itemView){
            super(itemView);
            commonMusicLl=itemView.findViewById(R.id.single_music_item_ll);
            musicIndex=itemView.findViewById(R.id.local_index);
            musicName=itemView.findViewById(R.id.local_music_name);
            musicSinger=itemView.findViewById(R.id.local_music_singer);
            menuIv=itemView.findViewById(R.id.single_music_menu);
        }

        @Override
        protected void bindView(MusicInfo musicInfo) {
            int position=getLayoutPosition();
            musicSinger.setText(musicInfo.getSinger());
            musicName.setText(musicInfo.getName());
            String s=String.valueOf(position+1);
            musicIndex.setText(s);
        }
    }
}
