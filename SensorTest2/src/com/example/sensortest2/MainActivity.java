package com.example.sensortest2;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener {

    View view;

    SensorManager sensorManager;
    Sensor gyro;
    Handler handler;
    float th = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyro,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        th += event.values[2] * 180 / 3.14159f;
    }

    private class DrawView extends View {
        private static final int REDRAW_MSEC = 50;

        private float width, height, cx, cy;
        private float rx = 500, ry = 100;

        private Paint paint;
        private Runnable runnable;

        public DrawView(Context context) {
            super(context);
            setFocusable(true);
            paint = new Paint();
            runnable = new Runnable() {
                public void run() {
                    invalidate();
                    handler.postDelayed(runnable, REDRAW_MSEC);
                }
            };
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            width = w;
            height = h;
            cx = width / 2;
            cy = height / 2;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            paint.setColor(Color.BLUE);
            canvas.rotate(th, cx, cy);
            canvas.drawRect(cx - rx / 2, cy - ry / 2, cx + rx / 2, cy + ry / 2,
                    paint);
        }

        protected void onAttachedToWindow() {
            handler.postDelayed(runnable, REDRAW_MSEC);
        }
    }

}
