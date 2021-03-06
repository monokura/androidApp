package com.example.timer1;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    public static final int TIME_MIN = 3;
    public static final long TICK_INTERVAL_MSEC = 200;

    private TextView countView;
    private ProgressBar timerProgress;
    private MyCountDownTimer cdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int msec = TIME_MIN * 60 * 1000;
        countView = (TextView) this.findViewById(R.id.count_view);
        countView.setText(timeViewString(msec));
        timerProgress = (ProgressBar) this.findViewById(R.id.timer_progress);
        timerProgress.setMax(msec);
        this.findViewById(R.id.start_button).setOnClickListener(this);
        this.findViewById(R.id.cancel_button).setOnClickListener(this);
        cdt = new MyCountDownTimer(msec, TICK_INTERVAL_MSEC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start_button:
            cdt.start();
            break;
        case R.id.cancel_button:
            cdt.cancel();
            break;
        }
    }

    @SuppressLint("DefaultLocale")
    private String timeViewString(int msec) {
        int sec = msec / 1000;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Toast.makeText(MainActivity.this, "Finished", Toast.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            countView.setText(timeViewString((int) millisUntilFinished));
            timerProgress.setProgress((int) millisUntilFinished);
        }

    }

}
