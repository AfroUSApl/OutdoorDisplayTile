package com.thomas.outdoortile;

import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class OutdoorTileService extends TileService {

    private void runRoot(String cmd) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(su.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            su.waitFor();
        } catch (Exception ignored) {}
    }

    private boolean isOutdoorOn() {
        try {
            Process p = Runtime.getRuntime().exec(
                    new String[]{"su","-c","settings get system display_outdoor_mode"});
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String result = reader.readLine();
            return "1".equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile != null) {
            boolean enabled = isOutdoorOn();
            tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setLabel(enabled ? "Outdoor ON" : "Outdoor OFF");
            tile.setSubtitle(enabled ? "Max brightness" : "Adaptive mode");

            tile.setIcon(android.graphics.drawable.Icon.createWithResource(
                    this,
                    enabled ? android.R.drawable.ic_menu_day
                            : android.R.drawable.ic_menu_gallery
            ));

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
        updateTileState();
    }

    @Override
    public void onClick() {
        super.onClick();

        boolean enabled = isOutdoorOn();

        runRoot("settings put system display_outdoor_mode " + (enabled ? "0" : "1"));

        vibrate();

        Toast.makeText(this,
                enabled ? "Outdoor Mode OFF" : "Outdoor Mode ON",
                Toast.LENGTH_SHORT).show();

        updateTileState();
    }

    @Override
    public void onLongClick() {
        Intent intent = new Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}