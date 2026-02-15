package com.thomas.outdoortile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.DataOutputStream;

public class TimeoutReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(su.getOutputStream());
            os.writeBytes("settings put system display_outdoor_mode 0\n");
            os.writeBytes("exit\n");
            os.flush();
            su.waitFor();
        } catch (Exception ignored) {}
    }
}
