package me.mikasa.music.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import me.mikasa.music.R;
import me.mikasa.music.bean.MusicInfo;
import me.mikasa.music.util.LogUtil;
import woo.mikasa.lib.base.BaseRvAdapter;

/**
 * Created by mikasa on 2018/11/13.
 */
public class SingleMusicAdapter extends BaseRvAdapter<MusicInfo>implements SectionIndexer {
    private Context mContext;
    public SingleMusicAdapter(Context context){
        this.mContext=context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.layout_single_music,parent,false);
        return new SingleMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SingleMusicHolder)holder).bindView(mDataList.get(position));
    }
    class SingleMusicHolder extends BaseRvViewHolder {
        LinearLayout singleMusicLl;
        TextView musicIndex, musicName,musicSinger,letterIndex;
        ImageView menuIv;
        SingleMusicHolder(View itemView){
            super(itemView);
            singleMusicLl=itemView.findViewById(R.id.single_music_item_ll);
            musicIndex=itemView.findViewById(R.id.local_index);
            musicName=itemView.findViewById(R.id.local_music_name);
            musicSinger=itemView.findViewById(R.id.local_music_singer);
            letterIndex=itemView.findViewById(R.id.index_head_tv);
            menuIv=itemView.findViewById(R.id.single_music_menu);
        }

        @Override
        protected void bindView(MusicInfo musicInfo) {
            final int position=getLayoutPosition();
            musicSinger.setText(musicInfo.getSinger());
            musicName.setText(musicInfo.getName());
            String s=String.valueOf(position+1);
            musicIndex.setText(s);
            int section = getSectionForPosition(position);
            int firstPosition = getPositionForSection(section);
            if (firstPosition == position){
                letterIndex.setVisibility(View.VISIBLE);
                String letter=musicInfo.getFirstLetter();
                letterIndex.setText(letter);
            }else{
                letterIndex.setVisibility(View.GONE);
            }
            menuIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMusicMenuClickListener!=null){
                        mMusicMenuClickListener.onMusicMenuClick(position);
                    }
                }
            });
        }
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的item的位置
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getItemCount(); i++) {
            char firstChar = mDataList.get(i).getFirstLetter().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }
    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return mDataList.get(position).getFirstLetter().charAt(0);
    }
    private OnMusicMenuClickListener mMusicMenuClickListener;
    public void setOnMusicMenuClickListener(OnMusicMenuClickListener listener){
        this.mMusicMenuClickListener=listener;
    }
    public interface OnMusicMenuClickListener{
        void onMusicMenuClick(int pos);
    }
}
