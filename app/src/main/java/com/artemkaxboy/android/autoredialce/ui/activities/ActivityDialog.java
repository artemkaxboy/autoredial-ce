package com.artemkaxboy.android.autoredialce.ui.activities;

import com.artemkaxboy.android.autoredialce.ui.FloatViewManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityDialog extends AppCompatActivity {

    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;

    private FloatViewManager mFloatViewManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFloatViewManager = new FloatViewManager(this);
        mFloatViewManager.showFloatView();

        finish();
    }

    private boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri
                    .parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                mFloatViewManager.showFloatView();
            }
        }
    }
}
