package com.app.fish.catchreport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LiveTripMain extends AppCompatActivity {

    private static final String FISH_LAKES_DB = "FishAndLakes.db";
    private static final String WRITE_DATA_DB = "CatchDatabase.db";
    private static final String WRITE_CATCH_PHP = "http://zoebaker.name/android_write_catch/addTripInfo.php";
    private int tripNum;
    TripInfoStorage trip;
    ArrayList<Fish> fish = new ArrayList<>();
    ArrayList<String> fNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_trip_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        trip = (TripInfoStorage)i.getSerializableExtra("TripInfo");
        initialize();
    }

    public void initialize(){
        setStartTime();
        initializeFinishTrip();
        initializeAddFish();
        initializeEdit();
        checkForFish();
    }

    public void setStartTime(){
        TextView startTimeDisplay = (TextView)findViewById(R.id.tripStartTimeView);
        Date start = trip.getStartDate();
        String time = start.toString();
        String[] sections = time.split(" ");

        startTimeDisplay.setText(sections[3]);
    }

    public void initializeFinishTrip(){
        Button b = (Button)findViewById(R.id.finishTrip);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date end = new Date();
                trip.setEndDate(end);
                confirmDialog(v);
            }
        });
    }

    public void initializeAddFish(){
        Button b = (Button)findViewById(R.id.addFishButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LiveAddFishActivity.class);
                intent.putExtra("TripInfo", trip);
                startActivity(intent);
            }
        });
    }

    public void initializeEdit(){
        Button b = (Button)findViewById(R.id.editFishButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner fishList = (Spinner) findViewById(R.id.fishCaughtSpinner);
                String fishname = fishList.getSelectedItem().toString();
                int index = getIndex(fishname);
                Intent intent = new Intent(v.getContext(), LiveAddFishActivity.class);
                intent.putExtra("TripInfo", trip);
                intent.putExtra("Fish", index);
                startActivity(intent);

            }
        });
    }

    public void checkForFish(){
        Spinner fishList = (Spinner)findViewById(R.id.fishCaughtSpinner);
        if(trip.numFish() != 0){
            for(int i = 0; i < trip.numFish(); i++){
                fish.add(trip.getFish(i));
                String type = fish.get(i).getSpecies();
                int count = 1;
                if(fish.size() > 1){
                    for(int j = 0; j < i; j++){
                        if(fish.get(j).getSpecies().equals(type)){
                            count++;
                        }
                    }
                }
                fNameList.add(fish.get(i).getSpecies() + " " + count);
            }
            ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.spinner_layout_ws, fNameList);
            adapt.setDropDownViewResource(R.layout.spinner_layout_ws);
            fishList.setAdapter(adapt);
        }

    }

    public int getIndex(String fishName){
        for(int i = 0; i < fNameList.size(); i++){
            if(fNameList.get(i).equals(fishName)){
                return i;
            }
        }
        return 0;
    }

    private void updateFishInfoDatabase(TripInfoStorage info)
    {
        DatabaseHandler handler = new DatabaseHandler(this, WRITE_DATA_DB);
        try
        {
            handler.createDatabase();
            handler.openDatabase();
            //read use order by if needed to find last id and add 1 to it for new id
            //write trip info to trip table
            SQLiteCursor cursor = handler.runQuery("SELECT MAX(_id) FROM MainCatch", null);
            tripNum = 1;
            if(cursor.getCount() <= 0)
                cursor.close();
            else
            {
                cursor.moveToFirst();
                tripNum =  cursor.getInt(0) + 1;
                cursor.close();
            }
            String q = "INSERT INTO MainCatch (_id,reportid,userid,numfish,location,county,tripdate,starttime,endtime,weather,temperature,latitude,longitude)";
            q += " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String[] vals = new String[13];
            Lake lake = info.getLake();
            Date sdate = info.getStartDate();
            SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd");
            String date = form.format(sdate);
            Date edate = info.getEndDate();
            vals[0] = tripNum+"";
            vals[1] = vals[0];
            vals[2] = "0";
            vals[3] = info.numFish()+"";
            vals[4] = lake.getName();
            vals[5] = lake.getCounty();
            vals[6] = date;
            vals[7] = sdate.getHours()+":"+sdate.getMinutes();
            vals[8] = edate.getHours()+":"+edate.getMinutes();
            vals[9] = "none";
            vals[10] = "none";
            vals[11] = lake.getLat()+"";
            vals[12]= lake.getLong()+"";
            handler.getWritableDatabase().execSQL(q, vals);

            for(int i = 0; i < info.numFish(); i++)
            {

                Fish f = info.getFish(i);
                int qty = info.getFish(i).getQuantity();

                for(int j=0; j<qty; j++) {

                    SQLiteCursor c2 = handler.runQuery("SELECT MAX(_id) FROM FishCaught", null);
                    int num2 = 1;
                    if (c2.getCount() <= 0)
                        c2.close();
                    else {
                        c2.moveToFirst();
                        num2 = c2.getInt(0) + 1;
                        c2.close();
                    }

                    String fq = "INSERT INTO FishCaught (_id,reportid,fishnum,species,weight,length,harvest,tags,finclip,method,userid) ";
                    fq += "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                    String[] fvals = new String[11];
                    fvals[0] = num2 + "";
                    fvals[1] = tripNum + "";
                    fvals[2] = i + 1 + "";
                    fvals[3] = f.getSpecies();
                    fvals[4] = f.getWeight() + "";
                    fvals[5] = f.getLength() + "";
                    fvals[6] = f.isReleased() ? "0" : "1";
                    fvals[7] = f.isTagged() ? "1" : "0";
                    fvals[8] = "0";
                    fvals[9] = "none";
                    fvals[10] = "0";
                    handler.getWritableDatabase().execSQL(fq, fvals);
                }
            }
            handler.close();
        }
        catch(IOException e)
        {
            Toast t = Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_LONG);
            t.show();
        }
        catch(SQLiteException e)
        {
            Toast t = Toast.makeText(getApplicationContext(), "Query Error", Toast.LENGTH_LONG);
            t.show();
        }
    }


    private void updateOnlineDatabase()
    {
        /*
        Creates new instance of FishPoster, which runs in the background to prevent freezing when
        attempting to post to server when out of service
         */
        new FishPoster().execute();
    }

    public String makeJString()
    {
        String word = "{\"upload_fishes\":[";
        try {
            JSONObject job = new JSONObject();
            Date date = trip.getStartDate();
            job.put("reportid", tripNum+"");
            job.put("userid", "0");
            job.put("numfish", trip.numFish()+"");
            job.put("location", trip.getLake().getName());
            job.put("date", date.getYear()+"/"+date.getMonth()+"/"+date.getDay());
            job.put("starttime", date.getHours()+"");
            job.put("endtime", trip.getEndDate().getHours()+"");
            job.put("weather", "none");
            job.put("temp", "0");
            word += job.toString()+",";
            for(int i = 0; i < trip.numFish(); i++)
            {
                JSONObject job2 = new JSONObject();
                Fish cur = trip.getFish(i);
                job2.put("fishnum["+i+"]", (i+1)+"");
                job2.put("species["+i+"]", cur.getSpecies());
                job2.put("weight["+i+"]", cur.getWeight()+"");
                job2.put("length["+i+"]", cur.getLength()+"");
                job2.put("harvest["+i+"]", cur.isReleased() ? "0" : "1");
                job2.put("tags["+i+"]", cur.isTagged() ? "1" : "0");
                job2.put("finclip["+i+"]", "0");
                job2.put("method["+i+"]", "none");
                job2.put("timecaught["+i+"]", "0");
                word += job2.toString() + ",";
            }
            word = word.substring(0, word.length()-1);
            word += "]}";
        }catch(JSONException e){}
        return word;
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

    private class FishPoster extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String[] urls)
        {
            String jstring = makeJString();
            try {
                URL url = new URL(WRITE_CATCH_PHP);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                out.write(jstring);
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                line=in.readLine();
                if(line != null){
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(LiveTripMain.this, "Data posted!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                in.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void confirmDialog(View view){

        final View curview = view;
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Submit?");
        alert.setMessage("Are you sure you want to submit this report?");
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                        Intent intent = new Intent(curview.getContext(), DisplayTripInfo.class);
                        updateFishInfoDatabase(trip);
                        updateOnlineDatabase();
                        startActivity(intent);
                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        alert.dismiss();
                    }
                });

        alert.show();
    }

}
