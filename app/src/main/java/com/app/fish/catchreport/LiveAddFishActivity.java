package com.app.fish.catchreport;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LiveAddFishActivity extends BaseDrawerActivity {

    private TripInfoStorage trip;
    private int cur;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_add_fish);
        //super.makeDrawer();
        Intent i = getIntent();
        trip = (TripInfoStorage)i.getSerializableExtra("TripInfo");
        if(i.getIntExtra("Fish", -1) != -1){
            AddFishFragment fishFragment = AddFishFragment.newInstance(trip, cur);
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.replace(R.id.fragHolder, fishFragment, "FISH_FRAGMENT");
            trans.commit();
        }else {
            trip.addFish(new Fish());

            Bundle b = new Bundle();
            b.putString("lakeID", trip.getLake().getId() + "");

            AddFishFragment fishFragment = AddFishFragment.newInstance(trip, cur);
            //fishFragment.setArguments(b);

            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.add(R.id.fragHolder, fishFragment, "FISH_FRAGMENT");
            trans.commit();
        }

        Button submitButton = (Button) findViewById(R.id.submitFishButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LiveTripMain.class);
                intent.putExtra("TripInfo", trip);
                startActivity(intent);
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteFishButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete?")
                        .setMessage("Are you sure you want to remove this fish? This cannot be undone.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                trip.removeFish(cur);
                                Intent intent = new Intent(v.getContext(), LiveTripMain.class);
                                intent.putExtra("TripInfo", trip);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
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


}
