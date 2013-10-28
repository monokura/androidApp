package com.example.timer4;

import com.example.timer4.MainActivity;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
        RestartableCountDownTimer.Action {

    public static final int DEFAULT_TIME_MIN = 3;
    public static final long TICK_INTERVAL_MSEC = 200;
    public static final int REQ_FOR_MINUTES = 1;

    private TextView countView;
    private ProgressBar timerProgress;
    private Button startButton, resetButton;

    private RestartableCountDownTimer cdt;
    private int msec;

    private enum State {
        INITIALIZED, COUNTING, STOPPED, FINISHED
    };

    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msec = DEFAULT_TIME_MIN * 60 * 1000;
        cdt = new RestartableCountDownTimer(msec, TICK_INTERVAL_MSEC, this);

        countView = (TextView) this.findViewById(R.id.count_view);
        countView.setText(timeViewString(msec));
        timerProgress = (ProgressBar) this.findViewById(R.id.timer_progress);
        timerProgress.setMax(msec);
        startButton = (Button) this.findViewById(R.id.start_button);
        resetButton = (Button) this.findViewById(R.id.reset_button);
        startButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        resetButton.setEnabled(false);

        state = State.INITIALIZED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start_button:
            switch (state) {
            case INITIALIZED:
                startButton.setText(getString(R.string.stop_button_label));
                resetButton.setEnabled(false);
                cdt.start();
                state = State.COUNTING;
                break;
            case COUNTING:
                startButton.setText(getString(R.string.restart_button_label));
                resetButton.setEnabled(true);
                cdt.stop();
                state = State.STOPPED;
                break;
            case STOPPED:
                startButton.setText(getString(R.string.stop_button_label));
                resetButton.setEnabled(false);
                cdt.restart();
                state = State.COUNTING;
                break;
            default:
                assert false : "Invalid State";
            }
            break;
        case R.id.reset_button:
            switch (state) {
            case STOPPED:
            case FINISHED:
                countView.setText(timeViewString(msec));
                timerProgress.setProgress(msec);
                startButton.setText(getString(R.string.start_button_label));
                startButton.setEnabled(true);
                resetButton.setEnabled(false);
                cdt.reset();
                state = State.INITIALIZED;
                break;
            default:
                assert false : "Invalid State";
            }
        default:
            assert false : "Unkown Button";
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivityForResult(new Intent(this, SettingsActivity.class),
                    REQ_FOR_MINUTES);
            break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent result) {
        switch (reqCode) {
        case REQ_FOR_MINUTES:
            switch (resCode) {
            case Activity.RESULT_OK:
                Bundle values = result.getExtras();
                if (values != null) {
                    String minutes = values.getString("minutes");
                    msec = Integer.valueOf(minutes).intValue() * 1000 * 60;
                    countView.setText(timeViewString(msec));
                    timerProgress.setMax(msec);
                    timerProgress.setProgress(msec);
                    startButton.setText(getString(R.string.start_button_label));
                    startButton.setEnabled(true);
                    resetButton.setEnabled(false);
                    cdt.renew((long)msec);
                    state = State.INITIALIZED;
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
            }
        }
    }
    
    @SuppressLint("DefaultLocale")
    private String timeViewString(int msec) {
        int sec = msec / 1000;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    @Override
    public void onFinish() {
        Toast.makeText(MainActivity.this,
                getString(R.string.finished_toast_text), Toast.LENGTH_LONG)
                .show();
        startButton.setEnabled(false);
        resetButton.setEnabled(true);
        state = State.FINISHED;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        countView.setText(timeViewString((int) millisUntilFinished));
        timerProgress.setProgress((int) millisUntilFinished);
    }
}
