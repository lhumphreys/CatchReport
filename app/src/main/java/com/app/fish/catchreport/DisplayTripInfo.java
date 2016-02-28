package com.app.fish.catchreport;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
                String[] first = cursor.getString(3).split(":");
                int sHour = Integer.parseInt(first[0]);
                int sMinute = Integer.parseInt(first[1]);
                String[] second = cursor.getString(4).split(":");
                int eHour = Integer.parseInt(second[0]);
                int eMinute = Integer.parseInt(second[1]);
                int hourDif = eHour - sHour;
                int minDif = eMinute - sMinute;
                if(minDif < 0) {
                    minDif += 60;
                    hourDif -= 1;
                }
                Displayer temp = new Displayer(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        hourDif + "",
                        minDif + "",
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
                String tripString = each.reportId + ": " + each.lake + ", " + each.county + "\n" + each.date;
                tripString+=  ", " + each.time + " hours and " +each.minutes+ " minutes long";
                TextView tripText = new TextView(this);
                tripText.setText(tripString);
                TextView fishText = new TextView(this);
                for (FishDisplayer fd : each.fishlist) {
                    String fishString = "\t" + fd.species + "\n\t" + fd.length + "\t" + fd.weight + "\n";
                    fishText.setText(fishText.getText() + fishString);
                }
                tripText.setTextSize(24);
                fishText.setTextSize(20);
                //button to delete trip
                Button delete = new Button(this);
                delete.setText("-");
                delete.setTextSize(24);
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
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.addView(tripText, 0);
                row.addView(delete, 1);
                displayLayout.addView(row, 0);
                displayLayout.addView(fishText, 1);
            }
        }
        else
        {
            TextView textView = new TextView(this);
            textView.setText("There are no trips");
            textView.setTextSize(24);
            displayLayout.addView(textView);
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
        String lake, county, date, time, minutes, reportId;
        ArrayList<FishDisplayer> fishlist;

        public Displayer(String la, String c, String d, String t, String m, String id) {
            lake = la;
            county = c;
            date = d;
            time = t;
            minutes = m;
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
            weight = w.equals("0") ? "not specified" : w+" pounds";
            length = l.equals("0") ? "not specified" : l+" inches";
            harvest = h;
            tags = t;
        }
    }
}
