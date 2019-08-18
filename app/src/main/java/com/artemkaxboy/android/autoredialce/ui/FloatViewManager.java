package com.artemkaxboy.android.autoredialce.ui;

import com.artemkaxboy.android.autoredialce.R;
import com.artemkaxboy.android.autoredialce.Redialing;
import com.artemkaxboy.android.autoredialce.ui.activities.ActivityDialog;
import com.artemkaxboy.android.autoredialce.ui.activities.ActivityDialog.Action;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import java.lang.ref.WeakReference;

//  https://stackoverflow.com/a/53092436/1452052

public class FloatViewManager {

    private static final String LAST_X_POSITION = "last_float_x_position";

    private static final String LAST_Y_POSITION = "last_float_y_position";

    private static final int MIN_MOVE = 5;

    private static FloatViewManager sInstance;

    private boolean mIsFloatViewShowing;

    private WindowManager mWindowManager;

    private int mLastX;

    private int mLastY;

    private int mFirstX;

    private int mFirstY;

    private LayoutParams mFloatViewLayoutParams;

    private WeakReference<View> mFloatView;

    private View getFloatView() {
        return mFloatView == null ? null : mFloatView.get();
    }

    public static FloatViewManager getInstance() {
        if (sInstance == null) {
            synchronized (FloatViewManager.class) {
                if (sInstance == null) {
                    sInstance = new FloatViewManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * Returns existing FloatView or creates a new one, if it doesn't exist.
     *
     * @param activity to inflate new view
     * @return FloatView
     */
    private View getOrCreateFloatView(final Activity activity) {
        final View floatView = getFloatView();
        if (floatView != null) {
            return floatView;
        }
        createLayoutParams(activity);
        View newView = createFloatView(activity);
        mFloatView = new WeakReference<>(newView);
        return newView;
    }

    /**
     * Creates FloatView
     *
     * @param activity to inflate view
     * @return FloatView
     */
    @SuppressLint("ClickableViewAccessibility")
    private View createFloatView(final Activity activity) {
        final View newView = View.inflate(activity, R.layout.approver, null);

        final OnTouchListener onTouchListener = (v, event) -> {
            int totalDeltaX = mLastX - mFirstX;
            int totalDeltaY = mLastY - mFirstY;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = (int) event.getRawX();
                    mLastY = (int) event.getRawY();
                    mFirstX = mLastX;
                    mFirstY = mLastY;
                    break;
                case MotionEvent.ACTION_UP:
                    if (!wasMoved(totalDeltaX, totalDeltaY)) {
                        return v.performClick();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) event.getRawX() - mLastX;
                    int deltaY = (int) event.getRawY() - mLastY;
                    mLastX = (int) event.getRawX();
                    mLastY = (int) event.getRawY();
                    if (wasMoved(totalDeltaX, totalDeltaY)
                            && event.getPointerCount() == 1) {
                        mFloatViewLayoutParams.x += deltaX;
                        mFloatViewLayoutParams.y += deltaY;
                        mWindowManager.updateViewLayout(newView, mFloatViewLayoutParams);
                    }
                    break;
                default:
                    break;
            }
            return true;
        };

        final OnClickListener onClickListener = v -> {
            dismissFloatView(v.getContext());
            Redialing.INSTANCE.approve();
        };

        ImageButton imageButton = newView.findViewById(R.id.image_button);
        imageButton.setOnClickListener(onClickListener);
        imageButton.setOnTouchListener(onTouchListener);

        newView.setOnTouchListener(onTouchListener);

        return newView;
    }

    /**
     * Creates layout params for new FloatView.
     *
     * @param activity to get windowManager and context to get last position preferences
     */
    @SuppressLint("RtlHardcoded")
    private void createLayoutParams(final Activity activity) {
        if (mFloatViewLayoutParams == null) {
            mWindowManager = activity.getWindowManager();

            final Point lastPosition = loadPosition(activity);

            final int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_TOAST;

            final int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    // without the following code the app saves absolute position, but then
                    // use it with offset of status bar
                    | (VERSION.SDK_INT >= VERSION_CODES.KITKAT
                    ? LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | LayoutParams.FLAG_TRANSLUCENT_STATUS
                    : 0);

            final int format = PixelFormat.TRANSLUCENT;

            mFloatViewLayoutParams = new LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    lastPosition.x, lastPosition.y,
                    type, flags, format);

            mFloatViewLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        }
    }

    /**
     * Runs new Activity to remove floatView if it is showing.
     *
     * @param context to run activity
     */
    public void dismissFloatView(Context context) {
        if (mIsFloatViewShowing) {
            Intent intent = new Intent(context, ActivityDialog.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Action.EXTRA_NAME, Action.CLOSE.getValue());
            context.startActivity(intent);
        }
    }

    /**
     * Removes FloatView from windowManager if it exists and is showing.
     *
     * @param activity to perform operation in UI thread
     */
    public void dismissFloatView(final Activity activity) {
        if (mIsFloatViewShowing) {
            mIsFloatViewShowing = false;
            activity.runOnUiThread(() -> {
                final View floatView = getFloatView();
                if (floatView != null) {
                    savePosition(activity, floatView);
                    mWindowManager.removeViewImmediate(floatView);
                }
            });
        }
    }

    /**
     * Shows FloatView. This method is entry point for creating FloatView.
     * It require an activity to create view, so it starts the new activity to do this
     * and closes it immediately after work is done.
     *
     * @param context app context
     */
    public void showFloatView(Context context) {
        if (!mIsFloatViewShowing) {
            Intent intent = new Intent(context, ActivityDialog.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Action.EXTRA_NAME, Action.SHOW.getValue());
            context.startActivity(intent);
        }
    }

    /**
     * Shows FloatView. This method is entry point for creating FloatView.
     *
     * @param activity an activity to create Views and get WindowManager
     */
    public void showFloatView(final Activity activity) {
        if (!mIsFloatViewShowing) {
            final View floatView = getOrCreateFloatView(activity);

            mIsFloatViewShowing = true;
            activity.runOnUiThread(() -> {
                if (!activity.isFinishing()) {
                    mWindowManager.addView(floatView, mFloatViewLayoutParams);
                }
            });
        }
    }

    private boolean wasMoved(int deltaX, int deltaY) {
        return Math.abs(deltaX) >= MIN_MOVE || Math.abs(deltaY) >= MIN_MOVE;
    }

    private void savePosition(final Context context, @NonNull final View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);

        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(LAST_X_POSITION, xy[0])
                .putInt(LAST_Y_POSITION, xy[1])
                .apply();
    }

    private Point loadPosition(final Context context) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final int x = sp.getInt(LAST_X_POSITION, -1);
        final int y = x < 0 ? x : sp.getInt(LAST_Y_POSITION, -1);

        if (x < 0 || y < 0) {
            return getDisplayCenter();
        }
        return new Point(x, y);
    }

    private Point getDisplayCenter() {
        Point size = new Point();
        mWindowManager.getDefaultDisplay().getSize(size);
        return new Point(size.x / 2, size.y / 2);
    }
}
