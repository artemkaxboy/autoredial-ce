package com.artemkaxboy.android.autoredialce;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artemkaxboy.android.autoredialce.contacts.MyContact;

public class ActivityConfirm extends Activity implements OnClickListener, SensorEventListener {
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int MIN_VELOCITY = 800;
    private static final int SWIPE_MAX_OFF_PATH = 250;

    Context mContext;
    View mRed, mGrnDrag, mRedDrag;
    RelativeLayout mConfirmDialog;
    ImageView mGrn;
    GestureDetector mGestureDetector;
    View.OnTouchListener mGestureListener;
    float dragStartX;
    int maxPadding;
    String mNumber;
    SensorManager mSensorManager;
    Sensor mSensor;
    AsyncTask<Long, Void, Void> timeoutTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setWindowAnimations(0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        mContext = this;

        if (P.confirmSensor(mContext)) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }


        mConfirmDialog = (RelativeLayout) findViewById(R.id.confirmDialog);

        mGestureDetector = new GestureDetector(this, new MyGestureDetector());
        mGestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startDrag(v, event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dragging(v, event);
                        break;
                    case MotionEvent.ACTION_UP:
                        resetDrag();
                        if (testEnd(v, event))
                            return true;
                }

                return mGestureDetector.onTouchEvent(event);
            }
        };

        mGrn = (ImageView) findViewById(R.id.confirmGrn);
        mGrn.setOnTouchListener(mGestureListener);
        mGrn.setOnClickListener(this);
        mRed = findViewById(R.id.confirmRed);
        mRed.setOnTouchListener(mGestureListener);
        mRed.setOnClickListener(this);
        mGrnDrag = findViewById(R.id.confirmGrnDrag);
        mRedDrag = findViewById(R.id.confirmRedDrag);

        mNumber = getIntent().getStringExtra("number");
        String name = getIntent().getStringExtra("name");
        ((TextView) findViewById(R.id.confirmNumber)).setText(mNumber);
        ((TextView) findViewById(R.id.confirmName)).setText(name);
        if (!name.equals(getString(android.R.string.unknownName)))
            ((TextView) findViewById(R.id.confirmType)).setText(MyContact.getTypeByNumber(mContext, mNumber));

        timeoutTask = new AsyncTask<Long, Void, Void>() {
            @Override
            protected Void doInBackground(Long... params) {
                try {
                    Thread.sleep(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                finish();
            }
        }.execute(10000L);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        maxPadding = mConfirmDialog.getWidth() - mGrn.getWidth() - mGrn.getPaddingLeft();
    }

    void startDrag(View v, MotionEvent event) {
        View v2 = null;
        switch (v.getId()) {
            case R.id.confirmGrn:
                v2 = mGrnDrag;
                break;
            case R.id.confirmRed:
                v2 = mRedDrag;
                break;
        }
        v.setVisibility(View.INVISIBLE);
        assert v2 != null;
        v2.setPadding(v.getPaddingLeft(),
                v.getPaddingTop(),
                v.getPaddingRight(),
                v.getPaddingBottom());
        v2.setVisibility(View.VISIBLE);
        dragStartX = event.getX();
    }

    void dragging(View v, MotionEvent event) {
        View v2 = null;
        float delta = event.getX() - dragStartX;
        int left = 0, right = 0;
        switch (v.getId()) {
            case R.id.confirmGrn:
                v2 = mGrnDrag;
                int padding = v.getPaddingLeft() + Math.round(delta);
                left = ((padding > maxPadding) ? maxPadding : padding);
                right = v.getPaddingRight();
                break;
            case R.id.confirmRed:
                v2 = mRedDrag;
                delta *= -1;
                padding = v.getPaddingRight() + Math.round(delta);
                left = v.getPaddingLeft();
                right = ((padding > maxPadding) ? maxPadding : padding);//v.getPaddingRight() + Math.round( delta );
                break;
        }
        if (delta > 0 && v2 != null )
            v2.setPadding(left, v.getPaddingTop(), right, v.getPaddingBottom());
    }

    boolean testEnd(View v, MotionEvent event) {
        float delta = event.getX() - dragStartX;
        switch (v.getId()) {
            case R.id.confirmGrn:
                int padding = v.getPaddingLeft() + Math.round(delta);
                if (padding >= maxPadding) {
                    green();
                    return true;
                }
                break;
            case R.id.confirmRed:
                delta *= -1;
                padding = v.getPaddingRight() + Math.round(delta);
                if (padding >= maxPadding) {
                    red();
                    return true;
                }
                break;
        }
        return false;
    }

    void resetDrag() {
        mGrn.setVisibility(View.VISIBLE);
        mGrnDrag.setVisibility(View.INVISIBLE);
        mRed.setVisibility(View.VISIBLE);
        mRedDrag.setVisibility(View.INVISIBLE);
    }

    void green() {
        finish();
        P.confirmIsGot(mContext, true);
        String number = "";
        for (int i = 0; i < mNumber.length(); i++) {
            char ch;
            if ((ch = mNumber.charAt(i)) == '#') {
                number += "%23";
                continue;
            }
            number += ch;
        }
        Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            startActivity(call);
        else
            Toast.makeText(mContext, "Cant call. Permission denied!", Toast.LENGTH_LONG).show();
	}
	void red() {
		finish();
	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ) {
			try {
				if( Math.abs( e1.getY() - e2.getY()) <= SWIPE_MAX_OFF_PATH ) {
					if( e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && velocityX * -1 > MIN_VELOCITY) {
						red();
						return true;
					} else if( e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && velocityX > MIN_VELOCITY ) {
						green();
						return true;
					}
				}
			} catch (Exception e) {
                e.printStackTrace();
            }
			return false;
		}
		@Override
		public boolean onSingleTapUp( MotionEvent e ) {
			
			return true;
		}
	}

	@Override public void onClick(View v) { }
	
	@Override
	protected void onResume() {
		super.onResume();
		if( P.confirmSensor( mContext ))
			mSensorManager.registerListener( this, mSensor, SensorManager.SENSOR_DELAY_NORMAL );
	}
	@Override
	protected void onPause() {
		super.onPause();
		if( P.confirmSensor( mContext ))
			mSensorManager.unregisterListener( this );
	}
	
	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy ) {}
	@Override
	public void onSensorChanged( SensorEvent e ) {
		if( e.values[0] == 0 ) green();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		timeoutTask.cancel( true );
	}
}
