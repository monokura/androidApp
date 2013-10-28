package com.example.timer5;

import android.os.CountDownTimer;

public class RestartableCountDownTimer {

    private static RestartableCountDownTimer instance = null;

    private enum State {
        INITIALIZED, COUNTING, STOPPED, FINISHED
    };

    private State state;

    private long millisInFuture, countDownInterval;
    private long remainingTime;
    private CountDownTimer timer;
    private Action action;

    public interface Action {
        public void onFinish();

        public void onTick(long millisUntilFinished);
    }

    private RestartableCountDownTimer() {
    }

    public static synchronized RestartableCountDownTimer getTimer(
            long millisInFuture, long countDownInterval, Action action) {
        if (instance == null)
            instance = new RestartableCountDownTimer();
        instance.initialize(millisInFuture, countDownInterval, action);
        return instance;
    }

    public static RestartableCountDownTimer getCurrentTimer(Action action)
            throws InstantiationException {
        if (instance == null)
            throw new InstantiationException();
        instance.setAction(action);
        return instance;
    }

    private void initialize(long millisInFuture, long countDownInterval,
            Action action) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.action = action;
        if (timer != null)
            timer.cancel();
        timer = newTimer(millisInFuture);
        state = State.INITIALIZED;
    }

    private void setAction(Action action) {
        this.action = action;
    }

    private CountDownTimer newTimer(long millisInFuture) {
        return new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onFinish() {
                action.onFinish();
                state = State.FINISHED;
            }

            @Override
            public void onTick(long millisUntilFinished) {
                action.onTick(millisUntilFinished);
                remainingTime = millisUntilFinished;
            }
        };
    }

    public void start() {
        if (state == State.INITIALIZED) {
            timer.start();
            state = State.COUNTING;
        }
    }

    public void stop() {
        if (state == State.COUNTING) {
            timer.cancel();
            state = State.STOPPED;
        }
    }

    public void restart() {
        if (state == State.STOPPED) {
            timer = newTimer(remainingTime);
            timer.start();
            state = State.COUNTING;
        }
    }

    public void reset() {
        if (state == State.STOPPED || state == State.FINISHED) {
            timer = newTimer(millisInFuture);
            state = State.INITIALIZED;
        }
    }

    public void renew(long millis) {
        if (timer != null) {
            millisInFuture = millis;
            remainingTime = millis;
            timer.cancel();
            timer = newTimer(millisInFuture);
            state = State.INITIALIZED;
        }
    }
}
