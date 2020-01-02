package com.z5x.timerview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.z5x.timer_view.TimerTextView;

public class MainActivity extends AppCompatActivity {

    private TimerTextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        view = findViewById(R.id.timer);

        view.start(System.currentTimeMillis() + 18 * 1000);

        view.setCountDownListener(new TimerTextView.CountDownListener() {
            @Override
            public void onTick(long seconds) {
                view.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {

            }
        });
    }
}
