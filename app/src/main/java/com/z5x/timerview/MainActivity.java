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

        view.start(18 * 1000);

    }
}
