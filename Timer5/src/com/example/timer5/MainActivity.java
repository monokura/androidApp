package com.example.timer5;

import com.example.timer5.MainActivity;

import android.media.AudioManager;
import android.media.SoundPool;
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

    private enum State {
        INITIALIZED, COUNTING, STOPPED, FINISHED
    };

    // state variables to be saved/resumed
    private State state;
    private int msec;
    private long millisUntilFinished;

    // singleton object (thread)
    private RestartableCountDownTimer cdt;

    private TextView countView;
    private ProgressBar timerProgress;
    private Button startButton, resetButton;
    private SoundPool soundPool;
    private int bell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TRACE", "onCreate() " + this);

        setContentView(R.layout.activity_main);

        countView = (TextView) this.findViewById(R.id.count_view);
        timerProgress = (ProgressBar) this.findViewById(R.id.timer_progress);
        startButton = (Button) this.findViewById(R.id.start_button);
        resetButton = (Button) this.findViewById(R.id.reset_button);
        startButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        resetButton.setEnabled(false);

        soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        bell = soundPool.load(this, R.raw.bell, 1);

		
		Toast.makeText(this, "Set SE", Toast.LENGTH_SHORT).show();
        
        if (savedInstanceState != null) {
            state = (State) savedInstanceState.getSerializable("state");
            msec = savedInstanceState.getInt("msec");
            millisUntilFinished = savedInstanceState
                    .getLong("millisUntilFinished");
            Log.d("DEBUG", "resumed state=" + state);
            try {
                cdt = RestartableCountDownTimer.getCurrentTimer(this);
            }
            catch (InstantiationException e) {
                assert false : "No timer instance";
            }
        }
        else {
            state = State.INITIALIZED;
            msec = DEFAULT_TIME_MIN * 60 * 1000;
            millisUntilFinished = msec;
            cdt = RestartableCountDownTimer.getTimer(msec, TICK_INTERVAL_MSEC,
                    this);
        }

        refreshDisplay();
        restoreButtonState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("TRACE", "onSaveInstanceState()");
        outState.putSerializable("state", state);
        outState.putInt("msec", msec);
        outState.putLong("millisUntilFinished", millisUntilFinished);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
                    millisUntilFinished = msec;
                    cdt.renew((long) msec);
                    state = State.INITIALIZED;
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
            }
        }
        refreshDisplay();
        restoreButtonState();
    }

    // OnClickListener

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.start_button:
            switch (state) {
            case INITIALIZED:
                cdt.start();
                state = State.COUNTING;
                break;
            case COUNTING:
                cdt.stop();
                state = State.STOPPED;
                break;
            case STOPPED:
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
                cdt.reset();
                state = State.INITIALIZED;
                millisUntilFinished = msec;
                break;
            default:
                assert false : "Invalid State";
            }
        default:
            assert false : "Unkown Button";
        }
        refreshDisplay();
        restoreButtonState();
    }

    // RestartableCountDownTimer.Action

    @Override
    public void onFinish() {
        Toast.makeText(MainActivity.this,
                getString(R.string.finished_toast_text), Toast.LENGTH_LONG)
                .show();
        soundPool.play(bell, 1.0f, 1.0f, 0, 0, 1.0f);
        state = State.FINISHED;
        restoreButtonState();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.millisUntilFinished = millisUntilFinished;
        refreshDisplay();
    }

    // misc. methods

    @SuppressLint("DefaultLocale")
    private String timeViewString(int msec) {
        int sec = msec / 1000;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    private void refreshDisplay() {
        if (state == State.INITIALIZED)
            timerProgress.setMax(msec);
        countView.setText(timeViewString((int) millisUntilFinished));
        timerProgress.setProgress((int) millisUntilFinished);
    }

    private void restoreButtonState() {
        switch (state) {
        case INITIALIZED:
            startButton.setEnabled(true);
            startButton.setText(getString(R.string.start_button_label));
            resetButton.setEnabled(false);
            break;
        case COUNTING:
            startButton.setEnabled(true);
            startButton.setText(getString(R.string.stop_button_label));
            resetButton.setEnabled(false);
            break;
        case STOPPED:
            startButton.setEnabled(true);
            startButton.setText(getString(R.string.restart_button_label));
            resetButton.setEnabled(true);
            break;
        case FINISHED:
            startButton.setEnabled(false);
            resetButton.setEnabled(true);
            break;
        }
    }

}
