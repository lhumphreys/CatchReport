package com.app.fish.catchreport;

import android.app.ActionBar;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayTripInfo extends BaseDrawerActivity {

    public static final String CATCH_DATA_DB = "CatchDatabase.db";
    private ArrayList<Displayer> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_trip_info);
        super.makeDrawer();



        getInfo();
        displayInfo();
        setDoneButton();
    }

    private void getInfo() {
        reportList = new ArrayList<Displayer>();
        DatabaseHandler help = new DatabaseHandler(this, CATCH_DATA_DB);
        try{
            help.createDatabase();
            help.openDatabase();
            String q = "SELECT location,county,tripdate,starttime,endtime,reportid FROM MainCatch";
            SQLiteCursor cursor = help.runQuery(q, null);
            while(cursor.moveToNext()) {
                Displayer temp = new Displayer(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        (cursor.getInt(4) - cursor.getInt(3)) + "",
                        cursor.getString(5));
                q = "SELECT species,weight,length,harvest,tags FROM FishCaught WHERE reportid=?";
                SQLiteCursor otherCursor = help.runQuery(q, new String[]{cursor.getString(5)});
                while (otherCursor.moveToNext()) {
                    FishDisplayer tempFish = new FishDisplayer(
                            otherCursor.getString(0),
                            otherCursor.getString(1),
                            otherCursor.getString(2),
                            otherCursor.getString(3),
                            otherCursor.getString(4));
                    temp.fishlist.add(tempFish);
                }
                reportList.add(temp);
            }
            help.close();
        }
        catch(SQLiteException e)
        {
            Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            Toast.makeText(this, "Error Creating Database", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Writes the Trip information into a text box.
     */
    private void displayInfo()

    {

        LinearLayout displayLayout = (LinearLayout) findViewById(R.id.DisplayLayout);
        if(reportList.size() > 0) {
            for (Displayer each : reportList) {

                String tripString = each.reportId + ": " + each.lake + ", " + each.county;
                String tripSpec = "Date: " + each.date + "\nDuration: " + each.time + " hours";


                TextView tripText = new TextView(this);
                TextView tripSpecText = new TextView(this);

                tripText.setText(tripString);
                tripSpecText.setText(tripSpec);

                tripText.setTextSize(24);
                tripSpecText.setTextSize(24);

                tripText.setGravity(Gravity.CENTER);

                tripText.setTextColor(getResources().getColor(android.R.color.white));
                tripSpecText.setBackground(getResources().getDrawable(R.drawable.background_rounded_corners_blue));

                TableRow.LayoutParams tripSpecParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,1.0f);
                tripSpecParams.setMargins(0,5,0,5);
                tripSpecText.setLayoutParams(tripSpecParams);
                tripSpecText.setPadding(15,5,5,5);


                //button to delete trip
                Button delete = new Button(this);

                delete.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LinearLayout row = (LinearLayout) view.getParent();
                                String entry = (String) ((TextView) row.getChildAt(0)).getText();
                                String[] vals = entry.split(":");
                                String id = vals[0].trim();
                                databaseDelete(id);
                                //THEN RELOAD PAGE
                                Intent i = new Intent(view.getContext(), DisplayTripInfo.class);
                                startActivity(i);
                            }
                        }
                );

                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
                TableRow.LayoutParams textParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,4.0f);

                buttonParams.setMargins(5,0,0,0);

                delete.setLayoutParams(buttonParams);
                tripText.setLayoutParams(textParams);

                tripText.setBackground(getResources().getDrawable(R.drawable.submit_button_rounded_blue));
                delete.setBackground(getResources().getDrawable(R.drawable.submit_button_rounded));
                delete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_delete,0,0,0);
                delete.setGravity(Gravity.CENTER);

                LinearLayout row = new LinearLayout(this);

                row.setOrientation(LinearLayout.HORIZONTAL);
                row.addView(tripText, 0);
                row.addView(delete,1);

                displayLayout.addView(row, 0);
                displayLayout.addView(tripSpecText,1);


                int i=2;

                for (FishDisplayer fd : each.fishlist) {

                    LinearLayout fishRow = new LinearLayout(this);
                    fishRow.setOrientation(LinearLayout.HORIZONTAL);

                    TextView fishText = new TextView(this);
                    TextView specText = new TextView(this);
                    fishText.setTextSize(20);
                    specText.setTextSize(20);


                    String fishString = fd.species;
                    String specString = fd.length + " inches\n" + fd.weight + " pounds";

                    fishText.setText(fishText.getText() + fishString);
                    fishText.setTextColor(getResources().getColor(android.R.color.white));
                    specText.setText(specText.getText() + specString);

                    TableRow.LayoutParams paramsFish = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,3.0f);
                    TableRow.LayoutParams paramsSpec = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,2.0f);

                    paramsFish.setMargins(0,0,5,5);
                    paramsSpec.setMargins(0,0,0,5);

                    fishText.setLayoutParams(paramsFish);
                    specText.setLayoutParams(paramsSpec);

                    specText.setBackground(getResources().getDrawable(R.drawable.background_rounded_corners_blue));
                    fishText.setBackground(getResources().getDrawable(R.drawable.selector_background_red));


                    fishRow.addView(fishText, 0);
                    fishRow.addView(specText, 1);

                    fishText.setGravity(Gravity.CENTER);
                    specText.setGravity(Gravity.CENTER);

                    displayLayout.addView(fishRow, i);

                    i++;
                }

                TextView whitespace = new TextView(this);
                whitespace.setText(whitespace.getText()+"\n\n");

                displayLayout.addView(whitespace,i);

            }
        }
    }

    private void databaseDelete(String reportID)
    {
        DatabaseHandler handler = new DatabaseHandler(this, CATCH_DATA_DB);
        handler.openDatabase();
        String query = "DELETE FROM MainCatch WHERE reportid=?";
        String args[] = new String[]{reportID};
        handler.getWritableDatabase().execSQL(query, args);
        query = "DELETE FROM FishCaught WHERE reportid=?";
        handler.getWritableDatabase().execSQL(query, args);
        handler.close();
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

    private class Displayer
    {
        String lake, county, date, time, reportId;
        ArrayList<FishDisplayer> fishlist;

        public Displayer(String la, String c, String d, String t, String id) {
            lake = la;
            county = c;
            date = d;
            time = t;
            reportId = id;
            fishlist = new ArrayList<FishDisplayer>();
        }
    }

    private class FishDisplayer
    {
        String species, weight, length, harvest, tags;

        public FishDisplayer(String s, String w, String l, String h, String t)
        {
            species = s;
            weight = w;
            length = l;
            harvest = h;
            tags = t;
        }
    }
}
