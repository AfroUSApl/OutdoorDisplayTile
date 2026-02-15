package com.thomas.outdoortile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class OutdoorTileService extends TileService {

    private static final long OUTDOOR_TIMEOUT = 15 * 60 * 1000;
    private boolean lastKnownState = false;

    private String runRootRead(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su","-c",cmd});
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private void runRootWrite(String cmd) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(su.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            su.waitFor();
        } catch (Exception ignored) {}
    }

    private boolean readRealState() {
        String result = runRootRead("settings get system display_outdoor_mode");
        return "1".equals(result);
    }

    private void scheduleTimeout() {
        Intent intent = new Intent(this, TimeoutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + OUTDOOR_TIMEOUT,
                    pendingIntent
            );
        }
    }

    private void cancelTimeout() {
        Intent intent = new Intent(this, TimeoutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void updateTileUI(boolean enabled) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setLabel(enabled ? "Outdoor ON" : "Outdoor OFF");
            tile.setSubtitle(enabled ? "Max brightness" : "Adaptive mode");
            tile.updateTile();
        }
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            v.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTileUI(lastKnownState);
    }

    @Override
    public void onClick() {
        super.onClick();

        boolean currentState = readRealState();

        if (currentState) {
            runRootWrite("settings put system display_outdoor_mode 0");
            cancelTimeout();
            lastKnownState = false;
            Toast.makeText(this, "Outdoor Mode OFF", Toast.LENGTH_SHORT).show();
        } else {
            runRootWrite("settings put system display_outdoor_mode 1");
            scheduleTimeout();
            lastKnownState = true;
            Toast.makeText(this, "Outdoor Mode ON (15 min)", Toast.LENGTH_SHORT).show();
        }

        vibrate();
        updateTileUI(lastKnownState);
    }
}
