package com.app.fish.catchreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayTripInfo extends AppCompatActivity {

    private TripInfoStorage info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getInfo();
        displayInfo();
        setDoneButton();
    }

    private void getInfo()
    {
        //WILL READ FROM DATABASE
        Intent intent = getIntent();
        TripInfoStorage i = (TripInfoStorage)intent.getSerializableExtra("TripInfo");
        info = i;
    }

    /*
    Writes the Trip information into a text box.
     */
    private void displayInfo()

    {
        TextView title = (TextView)findViewById(R.id.DisplayTitle);
        Lake lake = info.getLake();
        String word = lake.getName() + ", " + lake.getAbbreviation();
        if(lake.getRestriction())
            word += " (R)";
        title.setText(word);

        TextView body = (TextView)findViewById(R.id.DisplayBody);
        body.setText("");
        for(int i = 0; i < info.numFish(); i++)
        {
            String newWord = "";
            Fish curFish = info.getFish(i);
            newWord += curFish.getSpecies() + "\n\tWeight: " +curFish.displayWeight() +
                    "\n\tLength: "+curFish.displayLength() + "\n\tReleased: ";
            newWord += curFish.isReleased() ? "Yes\n\tTagged" : "No\n\tTagged";
            newWord += curFish.isTagged() ? "Yes\n\n" : "No\n\n";
            body.setText(body.getText() + newWord);
        }
    }

    /*
    Done button is set to go back to MainActivity. The TripInfoStorage object is not saved.
     */
    private void setDoneButton()
    {
        Button b = (Button)findViewById(R.id.DisplayDone);
        b.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), MainActivity.class);
                        startActivity(i);
                    }
                }
        );
    }

}
