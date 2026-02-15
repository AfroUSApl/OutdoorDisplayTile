package com.thomas.outdoortile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DisplayRedirectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS);
        startActivity(intent);
        finish();
    }
}
