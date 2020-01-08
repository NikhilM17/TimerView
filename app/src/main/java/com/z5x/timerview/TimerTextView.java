package com.z5x.timerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerTextView extends AppCompatTextView {

    private int timePattern = 0, daysPattern = 0, countDownSeconds = 10;
    private long endTime;
    private String expiryMessage;
    private boolean isTimerExpired = false, showTimeLeft;
    private Timer timer;
    private CountDownListener countDownListener;
    private int countDownTextColor;
    private int expiryMesgTextColor;
    private boolean hideWhenExpired = false;

    public TimerTextView(Context context) {
        this(context, null);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerTextView, defStyleAttr, 0);

        try {
            expiryMessage = a.getString(R.styleable.TimerTextView_expiry_message);
            timePattern = a.getInt(R.styleable.TimerTextView_time_pattern, 0);
            daysPattern = a.getInt(R.styleable.TimerTextView_days_pattern, 0);
            countDownSeconds = a.getInt(R.styleable.TimerTextView_countDownSeconds, -1);
            countDownTextColor = a.getColor(R.styleable.TimerTextView_countDownTextColor, Color.BLACK);
            expiryMesgTextColor = a.getColor(R.styleable.TimerTextView_expiryMesgTextColor, Color.BLACK);
            hideWhenExpired = a.getBoolean(R.styleable.TimerTextView_hideWhenExpired, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    private void createTimer() {
        if (endTime > 0) {
            timer = new Timer(endTime, 1000) {
                @Override
                public void onTick(long l) {
                    Log.w("Time", l + "");
                    setTime(l);
                    setTimerExpired(false);
                }

                @Override
                public void onFinish() {
                    onExpired();
                    if (countDownListener != null)
                        countDownListener.onFinish();
                }
            };
        }

    }

    private void setTime(long l) {

        long days = TimeUnit.MILLISECONDS.toDays(l);
        long secs = TimeUnit.MILLISECONDS.toSeconds(l) % 60;
        long mins = TimeUnit.MILLISECONDS.toMinutes(l) % 60;
        long hrs = TimeUnit.MILLISECONDS.toHours(l) % 24;

        if (days == 0 && hrs == 0 && mins == 0 && secs <= countDownSeconds) {
            setText(String.format(Locale.US, "%02d", secs));
            if (countDownListener != null) {
                countDownListener.onTick(secs);
            }
            setTextColor(countDownTextColor == 0 ? Color.RED : countDownTextColor);
        } else {
            setTextColor(Color.BLACK);
            timeCalculation(days, hrs, mins, secs);
        }
    }

    private void timeCalculation(long days, long hrs, long mins, long secs) {
        switch (timePattern) {
            case 0:  // D_HH_MM_SS
                String strDays = days != 1 ? "Days" : "Day";
                if (daysPattern == 0) {
                    setText(String.format(Locale.US, "%d %s, %02d:%02d:%02d", days, strDays, hrs, mins, secs));
                } else {
                    setText(String.format("%s %s", days, strDays));
                }
                break;

            case 1: // d_hh_mm_ss
                if (days == 0 && hrs > 0) {
                    setText(String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs));
                } else if (days == 0 && hrs == 0 && mins > 0) {
                    setText(String.format(Locale.US, "%02d:%02d", mins, secs));
                } else if (days == 0 && hrs == 0 && mins == 0) {
                    setText(String.format(Locale.US, "%02d", secs));
                }
                break;

            case 2:
                setText(String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs));
                break;
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private void onExpired() {
        hideWhenExpired = !TextUtils.isEmpty(expiryMessage);
        if (hideWhenExpired)
            setText(expiryMessage);
        setCompoundDrawablePadding(16);
        isTimerExpired = true;
        setTextColor(expiryMesgTextColor == 0 ? Color.BLACK : expiryMesgTextColor);
    }

    public void start(long seconds) {
        this.endTime = seconds * 1000;
        createTimer();
        timer.start();
    }

    public boolean isTimerExpired() {
        return isTimerExpired;
    }

    public void setTimerExpired(boolean timerExpired) {
        isTimerExpired = timerExpired;
    }

    public void stop() {
        if (timer != null)
            timer.cancel();
    }

    public void setCountDownListener(CountDownListener countDownListener) {
        this.countDownListener = countDownListener;
    }

    public interface CountDownListener {
        void onTick(long seconds);

        void onFinish();
    }
}