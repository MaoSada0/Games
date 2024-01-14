package com.example.games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class fisrtScreen extends AppCompatActivity {

    NumberPicker npHeight;
    NumberPicker npWidth;

    int W;
    int H;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisrt_screen);

        Button toMS = (android.widget.Button) findViewById(R.id.toMS);
        Button toTTT = (Button) findViewById(R.id.toTTT);

        npHeight = findViewById(R.id.NPHeight);
        npWidth = findViewById(R.id.NPWidth);

        npHeight.setMinValue(3);
        npWidth.setMinValue(3);

        npHeight.setMaxValue(100);
        npWidth.setMaxValue(100);

        npWidth.setValue(9);
        npHeight.setValue(9);

        H = npHeight.getValue();
        W = npWidth.getValue();


        npHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                H = newVal;
            }
        });

        npWidth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                W = newVal;
            }
        });


        toMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fisrtScreen.this, MineSweeper.class);
                intent.putExtra("height", H);
                intent.putExtra("width", W);
                startActivity(intent);
            }
        });

        toTTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fisrtScreen.this, TicTacToe.class);
                startActivity(intent);
            }
        });
    }
}