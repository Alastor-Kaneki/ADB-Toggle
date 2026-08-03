package com.alastorkaneki.adbtoggle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DebuggingStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DebuggingController.refreshAll(context);
    }
}
