package me.mikasa.music.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.mikasa.music.R;
import me.mikasa.music.bean.Music;
import woo.mikasa.lib.base.BaseRvAdapter;

/**
 * Created by mikasa on 2018/11/14.
 */
public class MusicItemAdapter extends BaseRvAdapter<Music> {
    private Context mContext;
    public MusicItemAdapter(Context context){
        this.mContext=context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.item_music,parent,false);
        return new MusicItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MusicItemHolder)holder).bindView(mDataList.get(position));
    }
    class MusicItemHolder extends BaseRvViewHolder{
        ImageView musicIv;
        TextView musicTitle,musicArtist;
        MusicItemHolder(View itemView){
            super(itemView);
            musicIv=itemView.findViewById(R.id.iv_music);
            musicTitle=itemView.findViewById(R.id.music_text_title);
            musicArtist=itemView.findViewById(R.id.music_text_artist);
        }

        @Override
        protected void bindView(Music music) {
            musicTitle.setText(music.getTitle());
            musicArtist.setText(music.getArtist());
            Glide.with(mContext).load(music.getImgId())
                    .crossFade(1600)
                    .error(R.drawable.ic_bili)
                    .into(musicIv);
        }
    }
}
