package com.app.fish.catchreport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ProgressBar;

public class MainActivity extends BaseDrawerActivity{

    ProgressBar spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.makeDrawer();

        final SharedPreferences prefs = this.getSharedPreferences(this.getPackageName(),this.MODE_PRIVATE);
        ScrollView sv = (ScrollView)findViewById(R.id.myscrollview);
        sv.fling(10);

        if(prefs.contains("FishAppAuth")==false)
        {
            Intent intent = new Intent(this.getBaseContext(),Login.class);
            startActivity(intent);
        }
        else if (prefs.getBoolean("FishAppAuth",false) == false)
        {
            Intent intent = new Intent(this.getBaseContext(),Login.class);
            startActivity(intent);
        }
        else{
            //
            Button addFishButton = (Button) findViewById(R.id.addFishButton);
            Button myReportsButton = (Button) findViewById(R.id.myReports);
            Button weeklyStats = (Button) findViewById(R.id.weeklyStats);
            Button askBiologist = (Button) findViewById(R.id.askBiologist);
            Button logout = (Button) findViewById(R.id.logoutbutton);
            //
            //

            addFishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TripInfoPage.class);
                    startActivity(intent);
                }
            });

            myReportsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DisplayTripInfo.class);
                    startActivity(intent);
                }
            });

            weeklyStats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),WeeklyStats.class);
                    startActivity(intent);
                }
            });

            askBiologist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AskBiologist.class);
                    startActivity(intent);
                }
            });
            //
            logout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("FishAppAuth", false).commit();
                    prefs.edit().putString("FishAppID","").commit();
                    Intent intent = new Intent(v.getContext(),MainActivity.class);
                    startActivity(intent);
                }
            });



        }
    }

    @Override
    public void onBackPressed() {
    }
}
