package com.thomas.outdoortile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
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

    private static final long OUTDOOR_TIMEOUT = 10 * 60 * 1000; // 10 minutes

    // 🔹 Read real state using root
    private boolean readRealState() {
        try {
            Process p = Runtime.getRuntime().exec(
                    new String[]{"su","-c","settings get system display_outdoor_mode"});
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String result = reader.readLine();
            return result != null && result.trim().equals("1");
        } catch (Exception e) {
            return false;
        }
    }

    // 🔹 Write state using root
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

    // 🔹 Schedule timeout (NO exact alarm → no permission required)
    private void scheduleTimeout() {
        Intent intent = new Intent(this, TimeoutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + OUTDOOR_TIMEOUT,
                    pendingIntent
            );
        }
    }

    // 🔹 Cancel timeout
    private void cancelTimeout() {
        Intent intent = new Intent(this, TimeoutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    // 🔹 Update tile UI safely
    private void updateTileUI(boolean enabled) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setLabel(enabled ? "Outdoor ON" : "Outdoor OFF");
            tile.setSubtitle(enabled ? "Max brightness" : "Adaptive mode");
            tile.updateTile();
        }
    }

    // 🔹 Haptic feedback
    private void vibrate() {
        try {
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                v.vibrate(VibrationEffect.createOneShot(
                        40,
                        VibrationEffect.DEFAULT_AMPLITUDE
                ));
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        boolean currentState = readRealState();
        updateTileUI(currentState);
    }

    @Override
    public void onClick() {
        super.onClick();

        boolean currentState = readRealState();

        if (currentState) {
            runRootWrite("settings put system display_outdoor_mode 0");
            cancelTimeout();
            Toast.makeText(this, "Outdoor Mode OFF", Toast.LENGTH_SHORT).show();
        } else {
            runRootWrite("settings put system display_outdoor_mode 1");
            scheduleTimeout();
            Toast.makeText(this, "Outdoor Mode ON (15 min)", Toast.LENGTH_SHORT).show();
        }

        vibrate();

        // 🔹 Safe refresh (prevents Samsung QS crash)
        requestListeningState(
                this,
                new ComponentName(this, OutdoorTileService.class)
        );
    }
}