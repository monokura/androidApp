package com.example.timer2;

import android.os.CountDownTimer;

public class RestartableCountDownTimer {
    private long millisInFuture, countDownInterval;
    private long remainingTime;
    private CountDownTimer timer;
    private Action action;
    
    private enum State { init, counting, stopped, finished };
    
    State state = State.init;
    
    public interface Action {
        public void onFinish();
        public void onTick(long millisUntilFinished);
        public void onStart();
        public void onStop();
        public void onRestart();
        public void onReset();
    }

    public RestartableCountDownTimer(long millisInFuture, long countDownInterval, Action action) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.action = action;
        timer = newTimer(millisInFuture);
    }

    private CountDownTimer newTimer(long millisInFuture) {
        return new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onFinish() {
                action.onFinish();
                state = State.finished;
            }

            @Override
            public void onTick(long millisUntilFinished) {
                action.onTick(millisUntilFinished);
                remainingTime = millisUntilFinished;
            }
        };
    }

    public void start() {
        if (state == State.init) {
            timer.start();
            action.onStart();
            state = State.counting;
        }
    }

    public void stop() {
        if (state == State.counting) {
            timer.cancel();
            action.onStop();
            state = State.stopped;
        }
    }

    public void restart() {
        if (state == State.stopped) {
            timer = newTimer(remainingTime);
            timer.start();
            action.onRestart();
            state = State.counting;
        }
    }

    public void reset() {
        if (state == State.stopped || state == State.finished) { 
            timer = newTimer(millisInFuture);
            action.onReset();
            state = State.init;
        }
    }
}


