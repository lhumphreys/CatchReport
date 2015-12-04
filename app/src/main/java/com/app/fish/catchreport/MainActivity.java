package com.app.fish.catchreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity{

    ProgressBar spin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spin = (ProgressBar) findViewById(R.id.progressBar1);
        spin.setIndeterminate(true);

        Button addFishButton = (Button) findViewById(R.id.addFishButton);
        addFishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spin.setVisibility(View.VISIBLE);
                Intent intent = new Intent(v.getContext(), TripInfoPage.class);
                startActivity(intent);

            }
        });
    }



}
