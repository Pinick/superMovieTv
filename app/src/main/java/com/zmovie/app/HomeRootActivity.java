package com.zmovie.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zmovie.app.tablayout.TvTabLayout;

public class HomeRootActivity extends AppCompatActivity {

    private TvTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_root);


        tabLayout = findViewById(R.id.home_tab_layout);



    }
}
