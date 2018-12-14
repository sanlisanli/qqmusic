package me.mikasa.music.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.mikasa.music.R;
import me.mikasa.music.bean.MusicInfo;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.receiver.PlayerManagerReceiver;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;

public class PlayingPopWindow extends PopupWindow{
    private View view;
    private Activity activity;
    private TextView countTv;
    private RelativeLayout closeRv;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<MusicInfo> musicInfoList;
    private DbManager manager;

    public PlayingPopWindow(Activity activity) {
        super(activity);
        this.activity = activity;
        manager = DbManager.getInstance(activity);
        musicInfoList = MusicUtil.getCurrentPlaylist(activity);
        initView();
    }

    private void initView(){
        this.view = LayoutInflater.from(activity).inflate(R.layout.playbar_menu_window, null);
        this.setContentView(this.view);
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int height = (int)(size.y * 0.5);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(height);

        this.setFocusable(true);
        this.setOutsideTouchable(true);

        // 设置弹出窗体的背景
        this.setBackgroundDrawable(activity.getResources().getDrawable(R.color.accent_text));
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_window_animation);

        // 添加OnTouchListener监听判断获取触屏位置，如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        recyclerView =view.findViewById(R.id.playing_list_rv);
        adapter = new Adapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        closeRv =view.findViewById(R.id.playing_list_close_rv);
        countTv =view.findViewById(R.id.playing_list_count_tv);
        countTv.setText("("+musicInfoList.size()+")");
        closeRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
    //adapter
    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder{
            LinearLayout contentLl;
            TextView nameTv,singerTv;
            ImageView loveIv;

            public ViewHolder(View itemView) {
                super(itemView);
                this.contentLl =itemView.findViewById(R.id.palybar_list_item_ll);
                this.nameTv =itemView.findViewById(R.id.palybar_list_item_name_tv);
                this.singerTv =itemView.findViewById(R.id.palybar_list_item_singer_tv);
                this.loveIv=itemView.findViewById(R.id.item_rv_love);
            }
        }

        @Override
        public int getItemCount() {
            return musicInfoList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_playbar_rv_list,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {
            final MusicInfo musicInfo = musicInfoList.get(position);
            holder.nameTv.setText(musicInfo.getName());
            holder.singerTv.setText(musicInfo.getSinger());
            if (musicInfo.getId() == MusicUtil.getIntShared(Constant.KEY_ID)){
                holder.nameTv.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                holder.singerTv.setTextColor(activity.getResources().getColor(R.color.colorAccent));
            }else {
                holder.nameTv.setTextColor(activity.getResources().getColor(R.color.accent_text));
                holder.singerTv.setTextColor(activity.getResources().getColor(R.color.accent_text));
            }
            if (manager.isMusicFavor(musicInfo.getId())){
                holder.loveIv.setImageResource(R.drawable.love_red);
            }else {
                holder.loveIv.setImageResource(R.drawable.love_white);
            }

            holder.contentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = manager.getMusicPath(musicInfo.getId());
                    Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
                    intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
                    intent.putExtra(Constant.KEY_PATH, path);
                    activity.sendBroadcast(intent);
                    MusicUtil.setShared(Constant.KEY_ID,musicInfo.getId());
                    notifyDataSetChanged();
                }
            });
            holder.loveIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (manager.isMusicFavor(musicInfo.getId())){
                        holder.loveIv.setImageResource(R.drawable.love_white);
                        manager.removeMyLove(musicInfo.getId());
                    }else {
                        holder.loveIv.setImageResource(R.drawable.love_red);
                        manager.setMyLove(musicInfo.getId());
                    }
                }
            });

        }

    }



}
