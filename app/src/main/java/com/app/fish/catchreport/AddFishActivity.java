package com.app.fish.catchreport;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public class AddFishActivity extends AppCompatActivity {

    private static final String FISH_LAKES_DB = "FishAndLakes.db";
    private TripInfoStorage info;
    private int cur;
    private Button addButton;
    private Button prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fish);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        info = getTripInfo();
        this.cur = 0;
        info.addFish(this.cur, new Fish());
        AddFishFragment fishFragment = AddFishFragment.newInstance(info.getFish(this.cur));
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.add(R.id.fragHolder, fishFragment, "FISH_FRAGMENT");
        trans.commit();

        this.prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur--;
                Fish f = null;
                if(cur>0){
                    f = info.getFish(cur);
                }

                switchFish(f);
                checkButtons();
            }
        });


        this.addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fish f;
                cur++;
                if(cur == info.numFish())
                {
                    f = new Fish();
                    info.addFish(cur,f);
                }
                else
                {
                    f = info.getFish(cur);
                }

                switchFish(f);
                checkButtons();
            }
        });

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DisplayTripInfo.class);
                //updateFishInfoDatabase(info);

                //**//To be Removed
                    intent.putExtra("TripInfo", info);
                //**//To be Removed

                startActivity(intent);
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete?")
                        .setMessage("Are you sure you want to remove this fish? This cannot be undone.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                info.removeFish(cur);
                                Fish f;
                                if (info.numFish() > 0) {
                                    if (!(cur < info.numFish())) {
                                        cur--;
                                    }
                                    f = info.getFish(cur);
                                } else {
                                    f = new Fish();
                                    info.addFish(f);
                                    cur = info.indexOf(f);
                                }
                                switchFish(f);
                                checkButtons();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
    }

    private void switchFish(Fish f){
        AddFishFragment fishFragment = AddFishFragment.newInstance(info.getFish(this.cur));
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.fragHolder, fishFragment, "FISH_FRAGMENT");
        trans.commit();
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Caution")
                .setMessage("Are you sure you want to return without submitting? Changes will not be saved.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No",null)
                .setCancelable(true)
                .create()
                .show();
    }

    private void checkButtons(){
        if(this.cur > 0){
            this.prevButton.setVisibility(View.VISIBLE);
        }
        else
        {
            this.prevButton.setVisibility(View.INVISIBLE);
        }
        if(this.cur < info.numFish() - 1)
        {
            this.addButton.setText("Next");
        }
        else
        {
            this.addButton.setText("Add Fish");
        }
    }

    /*
    This method is used to get the TripInfoStorage object from the TripInfoPage containing
    the location, date and time of the trip.

    The fish objects created are added to the fish list in the TripInfoStorage object.

    The TripInfoStorage object is passed from the TripInfoPage by the Intent.
     */
    private TripInfoStorage getTripInfo()
    {
        Intent i = this.getIntent();
        TripInfoStorage info = (TripInfoStorage)i.getSerializableExtra("TripInfo");
        return info;
    }

    /*
    private void updateFishInfoDatabase(TripInfoStorage info)
    {

    }
    */

}
