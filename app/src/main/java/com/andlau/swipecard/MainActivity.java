package com.andlau.swipecard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeCardLayout mScl_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScl_layout = findViewById(R.id.swipe_card);
        List<SwipeBean> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(new SwipeBean());
        }
        mScl_layout.setShowCards(2).setAdapter(new MyAdapter(this, list));
    }
}