package me.mikasa.music.util;

import android.content.Context;
import android.content.Intent;

import me.mikasa.music.fragment.PlayBarFragment;
import me.mikasa.music.receiver.PlayerManagerReceiver;
import static me.mikasa.music.receiver.PlayerManagerReceiver.status;

/**
 * Created by mikasa on 2018/11/12.
 * updateUIThread专门用于更新playBar的UI
 */
public class UpdateUIThread extends Thread {
    private static final int delay=200;//更新延迟
    private int threadNumber;
    private Context mContext;
    private PlayerManagerReceiver playerManagerReceiver;
    //private int duration;
    //private int currentPosition;
    public UpdateUIThread(PlayerManagerReceiver receiver,Context context,int num){
        this.playerManagerReceiver=receiver;
        this.mContext=context;
        this.threadNumber=num;
    }

    @Override
    public void run() {
        try {
            while (playerManagerReceiver.getThreadNumber()==threadNumber){
                if (status==Constant.STATUS_STOP){
                    break;
                }
                if (status==Constant.STATUS_PLAY||status==Constant.STATUS_PAUSE){
                    if (!playerManagerReceiver.isPlaying()){
                        break;
                    }
                    int duration=playerManagerReceiver.getDuration();
                    int currentPosition=playerManagerReceiver.getCurrentPosition();
                    Intent intent = new Intent(PlayBarFragment.ACTION_UPDATE);
                    intent.putExtra(Constant.STATUS, Constant.STATUS_RUN);//status
                    intent.putExtra(Constant.KEY_DURATION, duration);//duration
                    intent.putExtra(Constant.KEY_CURRENT, currentPosition);//currentPosition
                    mContext.sendBroadcast(intent);
                }
                try {
                    Thread.sleep(delay);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
