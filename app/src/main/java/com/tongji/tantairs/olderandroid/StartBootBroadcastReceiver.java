package com.tongji.tantairs.olderandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by tantairs on 2015/7/15.
 */
public class StartBootBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent service1 = new Intent(context, MyService1.class);
            context.startService(service1);
        }
    }
}
