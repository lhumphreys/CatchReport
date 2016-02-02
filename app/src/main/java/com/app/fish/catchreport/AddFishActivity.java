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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AddFishActivity extends BaseDrawerActivity {

    private static final String FISH_LAKES_DB = "FishAndLakes.db";
    private static final String WRITE_DATA_DB = "CatchDatabase.db";
    private static final String WRITE_CATCH_PHP = "http://zoebaker.name/android_write_catch/addTripInfo.php";
    private static final String WRITE_FISH_PHP = "http://zoebaker.name/android_write_catch/addFishInfo.php";
    private TripInfoStorage info;
    private Fish currentFish;
    private int cur, tripNum, fishNum;
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
        writeOnline(WRITE_CATCH_PHP);
        //for fish in info, currentFish=fish, writeonline
        //use for(fishNum = 1; fishNum <= info.numFish(); fishNum++)
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

    private void writeOnline(String phpName)
    {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                phpName, new Response.Listener<String>(){
            public void onResponse(String response)
            {
                try{
                    JSONObject job = new JSONObject(response);
                    boolean error = job.getBoolean("error");
                    if(!error){
                        Toast.makeText(getApplicationContext(), "Data posted!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddFishActivity.this, DisplayTripInfo.class);
                        startActivity(intent);
                        finish();
                    }else{
                        String errorMessage = job.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMessage, Toast.LENGTH_LONG).show();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> params = null;
                if(getUrl().equals(WRITE_CATCH_PHP))
                    params = makeCatchParams();
                else if(getUrl().equals(WRITE_FISH_PHP))
                    params = makeFishParams();
                return params;
            }
        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(strReq);
    }

    private Map<String, String> makeCatchParams()
    {
        Map<String, String> params = new HashMap<>();
        Date start = info.getStartDate();
        Date end = info.getEndDate();
        params.put("reportid", tripNum+"");
        params.put("userid", "0");
        params.put("numfish", info.numFish()+"");
        params.put("location", info.getLake().getName());
        params.put("date", start.getYear()+"/"+start.getMonth()+"/"+start.getDay());
        params.put("starttime", start.getHours()+"");
        params.put("endtime", end.getHours()+"");
        params.put("weather", "none");
        params.put("temp", "none");
        return params;
    }

    private Map<String, String> makeFishParams()
    {
        Map<String, String> params = new HashMap<>();
        params.put("reportid", tripNum+"");
        params.put("fishnum", fishNum+"");
        params.put("species", currentFish.getSpecies());
        params.put("weight", currentFish.getWeight()+"");
        params.put("length", currentFish.getLength()+"");
        params.put("harvest", currentFish.isReleased() ? "0" : "1");
        params.put("tags", currentFish.isTagged() ? "1" : "0");
        params.put("finclip", "0");
        params.put("method", "none");
        params.put("timecaught", "0");
        return params;
    }

}
