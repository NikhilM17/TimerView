package com.z5x.timerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.timer_view.TimerTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimerTextView textView = findViewById(R.id.tvTimer);
        textView.start(10);

    }
}
