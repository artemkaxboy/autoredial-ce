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

    private static final Action sDefaultAction = Action.CLOSE;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Action action;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Action.EXTRA_NAME)) {
            action = Action
                    .getByValue(intent.getIntExtra(Action.EXTRA_NAME, sDefaultAction.getValue()));
        } else {
            action = sDefaultAction;
        }

        switch (action) {
            case SHOW:
                FloatViewManager.getInstance().showFloatView(this);
                break;
            case CLOSE:
                FloatViewManager.getInstance().dismissFloatView(this);
                break;
            default:
        }
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
//        if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
//          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
//                mFloatViewManager.showFloatView();
//            }
//        }
    }

    public enum Action {
        SHOW(0),
        CLOSE(1),
        ;

        public static final String EXTRA_NAME = "Action";

        private final int mValue;

        public int getValue() {
            return mValue;
        }

        Action(final int value) {
            mValue = value;
        }

        public static Action getByValue(int value) {
            for (Action a : values()) {
                if (a.getValue() == value) {
                    return a;
                }
            }
            return sDefaultAction;
        }
    }
}
