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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        final SharedPreferences prefs = this.getSharedPreferences(this.getPackageName(),this.MODE_PRIVATE);

        if(prefs.contains("FishAppAuth") == false)
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

            Button logout = (Button) findViewById(R.id.logoutbutton);
            TextView status = (TextView) findViewById(R.id.textView6);
            TextView id = (TextView) findViewById(R.id.textView7);
            //

            Button addFishButton = (Button) findViewById(R.id.addFishButton);

            //
            status.setText("Logged In: "+prefs.getBoolean("FishAppAuth",false));
            id.setText("User ID: "+prefs.getString("FishAppID", "Not Found"));
            //

            addFishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TripInfoPage.class);
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
        //
    }

    @Override
    public void onBackPressed() {
    }
}
