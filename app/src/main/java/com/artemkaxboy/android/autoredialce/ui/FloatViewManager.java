package com.artemkaxboy.android.autoredialce.ui;

import static android.content.Context.WINDOW_SERVICE;

import com.artemkaxboy.android.autoredialce.R;
import com.artemkaxboy.android.autoredialce.utils.Logger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class FloatViewManager {

    private static boolean mIsFloatViewShowing;

    private Activity mActivity;

    private WindowManager mWindowManager;

    private View mFloatView;

    private WindowManager.LayoutParams mFloatViewLayoutParams;

    private boolean mFloatViewTouchConsumedByMove = false;

    private int mFloatViewLastX;

    private int mFloatViewLastY;

    private int mFloatViewFirstX;

    private int mFloatViewFirstY;

    @SuppressLint("InflateParams")
    public FloatViewManager(Activity activity) {
        mActivity = activity;
        LayoutInflater inflater = LayoutInflater.from(activity);
        mFloatView = inflater.inflate(R.layout.approver, null);
//        ImageButton imageButton = mFloatView.findViewById(R.id.image_button);
//        imageButton.setOnClickListener(v -> dismissFloatView());
//        imageButton.setOnTouchListener(mFloatViewOnTouchListener);

        mFloatView.setOnClickListener(mFloatViewOnClickListener);
        mFloatView.setOnTouchListener(mFloatViewOnTouchListener);

        mFloatViewLayoutParams = new WindowManager.LayoutParams();
        mFloatViewLayoutParams.format = PixelFormat.TRANSLUCENT;
        mFloatViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mFloatViewLayoutParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_TOAST;

        mFloatViewLayoutParams.gravity = Gravity.CENTER;
        mFloatViewLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatViewLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }

    public void dismissFloatView() {
        if (mIsFloatViewShowing) {
            mIsFloatViewShowing = false;
            mActivity.runOnUiThread(() -> {
                if (mWindowManager != null) {
                    mWindowManager.removeViewImmediate(mFloatView);
                }
            });
        }
    }

    public void showFloatView() {
        if (!mIsFloatViewShowing) {
            mIsFloatViewShowing = true;
            mActivity.runOnUiThread(() -> {
                if (!mActivity.isFinishing()) {
                    mWindowManager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
                    if (mWindowManager != null) {
                        mWindowManager.addView(mFloatView, mFloatViewLayoutParams);
                    }
                }
            });
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private View.OnClickListener mFloatViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.runOnUiThread(
                    () -> dismissFloatView());
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private View.OnTouchListener mFloatViewOnTouchListener = new View.OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @TargetApi(Build.VERSION_CODES.FROYO)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams prm = mFloatViewLayoutParams;
            int totalDeltaX = mFloatViewLastX - mFloatViewFirstX;
            int totalDeltaY = mFloatViewLastY - mFloatViewFirstY;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    mFloatViewFirstX = mFloatViewLastX;
                    mFloatViewFirstY = mFloatViewLastY;
                    break;
                case MotionEvent.ACTION_UP:
                    mFloatViewTouchConsumedByMove = totalDeltaX > 0 | totalDeltaY > 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) event.getRawX() - mFloatViewLastX;
                    int deltaY = (int) event.getRawY() - mFloatViewLastY;
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    if (Math.abs(totalDeltaX) >= 5 || Math.abs(totalDeltaY) >= 5) {
                        if (event.getPointerCount() == 1) {
                            prm.x += deltaX;
                            prm.y += deltaY;
                            mFloatViewTouchConsumedByMove = true;
                            if (mWindowManager != null) {
                                mWindowManager.updateViewLayout(mFloatView, prm);
                            }
                        } else {
                            mFloatViewTouchConsumedByMove = false;
                        }
                    } else {
                        mFloatViewTouchConsumedByMove = false;
                    }
                    break;
                default:
                    break;
            }
            return mFloatViewTouchConsumedByMove;
        }
    };

}
