package com.thomas.outdoortile;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
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

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(isOutdoorOn() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean enabled = isOutdoorOn();
        runRoot("settings put system display_outdoor_mode " + (enabled ? "0" : "1"));

        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(enabled ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
            tile.updateTile();
        }
    }
}
