package com.z5x.timer_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerTextView extends AppCompatTextView {

    private boolean bgProgressEnabled;
    private int timePattern = 0, daysPattern = 0, cutOffSeconds = 10;
    private long endTime;
    private String expiryMessage;
    private boolean isTimerExpired = false;
    private CountDownTimer timer;
    private CountDownListener countDownListener;
    private boolean showTimeLeft;
    private Paint paint, linePaint;
    private float sweepAngle = 360;
    private float angle = 0;

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
            bgProgressEnabled = a.getBoolean(R.styleable.TimerTextView_backgroundEnable, false);
            cutOffSeconds = a.getInteger(R.styleable.TimerTextView_cutOffSeconds, 10);
            sweepAngle = sweepAngle / cutOffSeconds;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
        createTimer();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15f);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(15f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showTimeLeft && !isTimerExpired) {
            float width = (float) getWidth();
            float height = (float) getHeight();
            float radius;

            if (width > height) {
                radius = height / 4;
            } else {
                radius = width / 4;
            }

            Path path = new Path();
            path.addCircle(width / 2, height / 2, radius, Path.Direction.CW);

            float center_x, center_y;
            final RectF oval = new RectF();

            center_x = width / 2;
            center_y = height / 2;

            oval.set(center_x - radius,
                    center_y - radius,
                    center_x + radius,
                    center_y + radius);
            canvas.drawCircle(center_x, 150, 75, paint);
            canvas.drawArc(oval, -90, angle, false, linePaint);
        }
    }

    private void createTimer() {
        if (endTime > 0) {
            timer = new CountDownTimer(endTime - System.currentTimeMillis(), 1000) {
                @Override
                public void onTick(long l) {
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

        if (bgProgressEnabled) {
            showTimeLeft = days == 0 && hrs == 0 && mins == 0 && secs <= cutOffSeconds;
            if (days == 0 && hrs == 0 && mins == 0 && secs <= cutOffSeconds) {
                angle = sweepAngle * secs;
                Log.w("Angle", angle + "");
                invalidate();
                setText(String.format(Locale.US, "%02d", secs));
                if (countDownListener != null) {
                    countDownListener.onTick(secs);
                }
            } else {
                timeCalculation(days, hrs, mins, secs);
            }
        } else {
            timeCalculation(days, hrs, mins, secs);
        }
    }

    private void timeCalculation(long days, long hrs, long mins, long secs){
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
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            stop();
        } else {
            start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private void onExpired() {
        setText(expiryMessage);
        setCompoundDrawablePadding(16);
        isTimerExpired = true;
    }

    public void start() {

        if (endTime > System.currentTimeMillis()) {
            start(endTime);
        } else if (endTime > 0) {
            onExpired();
        } else {

        }

    }

    public void start(long endTime) {
        this.endTime = endTime;
        createTimer();

        if (endTime - System.currentTimeMillis() < 24 * 60 * 60 * 1000) {
            timer.start();
        } else {
            setTime(endTime - System.currentTimeMillis());
        }

    }

    /*public void start(long endTime, boolean showTimeLeft) {
        endTime = System.currentTimeMillis() + endTime;
        this.showTimeLeft = showTimeLeft;
        start(endTime);
    }*/

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