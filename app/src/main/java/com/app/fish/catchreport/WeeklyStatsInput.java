package com.app.fish.catchreport;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WeeklyStatsInput extends AppCompatActivity {

    public static final String FISH_LAKES_DB = "FishAndLakes.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Pick A Lake");
        setContentView(R.layout.activity_weekly_stats_input);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initializeList();
        initializeSubmit();

    }

    private void initializeSubmit()
    {
        Button b = (Button)findViewById(R.id.wsSubmit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.press);
                v.startAnimation(anim);

                /** new intent will be called on animation completion**/
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(getApplicationContext(), WeeklyStats.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });
    }

    private void initializeList()
    {
        ArrayList<String> counties;
        counties = fillCounties();
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.spinner_layout_ws, counties);
        adapt.setDropDownViewResource(R.layout.spinner_layout_ws);
        Spinner spin = (Spinner)findViewById(R.id.wsCountySpinner);
        spin.setAdapter(adapt);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private Spinner lakes = (Spinner) findViewById(R.id.wsLakeSpinner);
            private TextView title = (TextView) findViewById(R.id.wsTextViewCounty);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                lakes.setVisibility(Spinner.VISIBLE);
                title.setVisibility(TextView.VISIBLE);
                ArrayList<LakeEntry> lakeList = fillLakes((String) parent.getItemAtPosition(position));
                ArrayAdapter<LakeEntry> lakeAdapt = new ArrayAdapter<LakeEntry>(parent.getContext(), R.layout.spinner_layout_ws, lakeList);
                lakes.setAdapter(lakeAdapt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lakes.setVisibility(Spinner.GONE);
                title.setVisibility(TextView.GONE);
            }
        });
    }

    private ArrayList<String> fillCounties()
    {
        ArrayList<String> counties = new ArrayList<String>();
        DatabaseHandler help = null;
        try{
            help = new DatabaseHandler(this, FISH_LAKES_DB);
            help.createDatabase();
            help.openDatabase();
            SQLiteCursor cur = help.runQuery("SELECT DISTINCT County FROM Lakes", new String[0]);
            while(cur.moveToNext())
            {
                counties.add(cur.getString(0));
            }
            cur.close();
            help.close();
        }
        catch(Exception e)
        {
            counties.add("nothing");
            Exception ex =  e;
            if(help != null)
            {
                help.close();
            }
        }
        return counties;
    }

    public ArrayList<LakeEntry> fillLakes(String county)
    {
        ArrayList<LakeEntry> lakeNames = new ArrayList<LakeEntry>();
        DatabaseHandler db = new DatabaseHandler(this, FISH_LAKES_DB);
        db.openDatabase();
        SQLiteCursor cur = db.runQuery("SELECT WaterBodyName, _id FROM Lakes WHERE County=?", new String[]{county});
        while(cur.moveToNext())
        {
            lakeNames.add(new LakeEntry(cur.getString(0), cur.getInt(1)));
        }
        cur.close();
        db.close();
        return lakeNames;
    }

    private class LakeEntry
    {
        public String name;
        public int id;

        public LakeEntry(String n, int i)
        {
            name = n;
            id = i;
        }

        public String toString()
        {
            return name;
        }
    }

}
