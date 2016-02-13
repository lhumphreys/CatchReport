package com.app.fish.catchreport;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AddFishActivity extends BaseDrawerActivity {

    private static final String FISH_LAKES_DB = "FishAndLakes.db";
    private static final String WRITE_DATA_DB = "CatchDatabase.db";
    private static final String WRITE_CATCH_PHP = "http://zoebaker.name/android_write_catch/addTripInfo.php";
    private TripInfoStorage info;
    private int cur, tripNum;
    private Button addButton;
    private Button prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fish);
        super.makeDrawer();

        info = getTripInfo();
        this.cur = 0;
        info.addFish(this.cur, new Fish());

        Bundle b = new Bundle();
        b.putString("lakeID", info.getLake().getId()+"");

        AddFishFragment fishFragment = AddFishFragment.newInstance(info.getFish(this.cur));

        fishFragment.setArguments(b);

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
                updateFishInfoDatabase(info);
                updateOnlineDatabase();
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
            Date edate = info.getEndDate();
            vals[0] = tripNum+"";
            vals[1] = vals[0];
            vals[2] = "0";
            vals[3] = info.numFish()+"";
            vals[4] = lake.getName();
            vals[5] = lake.getCounty();
            vals[6] = sdate.getYear()+"/"+sdate.getMonth()+"/"+sdate.getDay();
            vals[7] = sdate.getHours()+"";
            vals[8] = edate.getHours()+"";
            vals[9] = "none";
            vals[10] = "none";
            vals[11] = lake.getLat()+"";
            vals[12]= lake.getLong()+"";
            handler.getWritableDatabase().execSQL(q, vals);
            for(int i = 0; i < info.numFish(); i++)
            {
                SQLiteCursor c2 = handler.runQuery("SELECT MAX(_id) FROM FishCaught", null);
                int num2 = 1;
                if(c2.getCount()<=0)
                    c2.close();
                else{
                    c2.moveToFirst();
                    num2 = c2.getInt(0)+1;
                    c2.close();
                }
                Fish f = info.getFish(i);
                String fq = "INSERT INTO FishCaught (_id,reportid,fishnum,species,weight,length,harvest,tags,finclip,method,userid) ";
                fq+="VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                String[] fvals = new String[11];
                fvals[0] = num2+"";
                fvals[1] = tripNum+"";
                fvals[2] = i+1+"";
                fvals[3] = f.getSpecies();
                fvals[4] = f.getWeight()+"";
                fvals[5] = f.getLength()+"";
                fvals[6] = f.isReleased() ? "0" : "1";
                fvals[7] = f.isTagged() ? "1" : "0";
                fvals[8] = "0";
                fvals[9] = "none";
                fvals[10] = "0";
                handler.getWritableDatabase().execSQL(fq, fvals);
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
            Date date = info.getStartDate();
            job.put("reportid", tripNum+"");
            job.put("userid", "0");
            job.put("numfish", info.numFish()+"");
            job.put("location", info.getLake().getName());
            job.put("date", date.getYear()+"/"+date.getMonth()+"/"+date.getDay());
            job.put("starttime", date.getHours()+"");
            job.put("endtime", info.getEndDate().getHours()+"");
            job.put("weather", "none");
            job.put("temp", "0");
            word += job.toString()+",";
            for(int i = 0; i < info.numFish(); i++)
            {
                JSONObject job2 = new JSONObject();
                Fish cur = info.getFish(i);
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

                            Toast.makeText(AddFishActivity.this, "Data posted!", Toast.LENGTH_SHORT).show();
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

}
