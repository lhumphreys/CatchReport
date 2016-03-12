package com.app.fish.catchreport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends BaseDrawerActivity {

    ProgressBar spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Fish N' Trips");
        setContentView(R.layout.activity_main);
        super.makeDrawer();
        final SharedPreferences prefs = this.getSharedPreferences(this.getPackageName(),this.MODE_PRIVATE);
        ScrollView sv = (ScrollView)findViewById(R.id.myscrollview);
        sv.fling(10);

        Map<String, ?> map = prefs.getAll();

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
            Button startReport = (Button) findViewById(R.id.liveReport);
            Button addFishButton = (Button) findViewById(R.id.addFishButton);
            Button myReportsButton = (Button) findViewById(R.id.myReports);
            Button weeklyStats = (Button) findViewById(R.id.weeklyStats);
            Button askBiologist = (Button) findViewById(R.id.askBiologist);
            Button logout = (Button) findViewById(R.id.logoutbutton);
            //
            //
            startReport.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                    v.startAnimation(anim);

                    /** new intent will be called on animation completion**/
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {

                            Intent intent = new Intent(getApplicationContext(), LiveTripBegin.class);
                            startActivity(intent);

                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            });

            addFishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                    v.startAnimation(anim);

                    /** new intent will be called on animation completion**/
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Intent intent = new Intent(getApplicationContext(), TripInfoPage.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            });

            myReportsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                    v.startAnimation(anim);

                    /** new intent will be called on animation completion**/
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Intent intent = new Intent(getApplicationContext(), DisplayTripInfo.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            });

            weeklyStats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                    v.startAnimation(anim);

                    /** new intent will be called on animation completion**/
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Intent intent = new Intent(getApplicationContext(), WeeklyStatsInput.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            });

            askBiologist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                    v.startAnimation(anim);

                    /** new intent will be called on animation completion**/
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Intent intent = new Intent(getApplicationContext(), AskBiologist.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            });
            //
            logout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.bounce);
                    v.startAnimation(anim);

                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            prefs.edit().putBoolean("FishAppAuth", false).commit();
                            prefs.edit().putString("FishAppID", "").commit();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            });



        }
    }

    @Override
    public void onBackPressed() {

        finish();
    }

}
