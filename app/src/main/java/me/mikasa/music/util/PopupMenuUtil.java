package me.mikasa.music.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import me.mikasa.music.view.PlayingPopWindow;

/**
 * Created by mikasa on 2018/11/16.
 */
public class PopupMenuUtil {
    public static void showPopupMenu(final Activity activity, View anchor){
        PlayingPopWindow playingPopWindow = new PlayingPopWindow(activity);
        playingPopWindow.showAtLocation(anchor, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha=0.7f;
        activity.getWindow().setAttributes(params);

        playingPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.alpha=1f;
                activity.getWindow().setAttributes(params);
            }
        });
    }
}
