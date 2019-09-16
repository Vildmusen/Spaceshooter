package com.viktorvilmusenaho.spaceshooter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button startButton = findViewById(R.id.btn_start);
        startButton.setOnClickListener(this);

        final TextView high_score = (TextView) findViewById(R.id.high_score_txt);
        SharedPreferences prefs = getSharedPreferences(UI.PREFS, Context.MODE_PRIVATE);
        int longestDistance = prefs.getInt(UI.LONGEST_DIST, 0);
        high_score.setText(String.format("Longest Distance Traveled: %skm", longestDistance));
    }

    @Override
    public void onClick(View v) {
        final Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
        finish();
    }
}
