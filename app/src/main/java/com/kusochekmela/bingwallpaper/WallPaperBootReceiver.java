package com.kusochekmela.bingwallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by kiseleva on 18.10.2017.
 */

public class WallPaperBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                WallPaperReceiver alarm = new WallPaperReceiver();
                alarm.setAlarm(context);
        }
    }
}
