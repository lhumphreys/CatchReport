package com.app.fish.catchreport;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is the start of the Live Trip sequence. It collects the Location, Date,
 * Starting Time, and any extras from the phone/user, and starts the trip.
 *
 * @version 1.0
 */

public class LiveTripBegin extends BaseDrawerActivity {

    public static final String FISH_LAKES_DB = "FishAndLakes.db";
    private TripInfoStorage trip;
    Exception ex;
    private Date tripDate;
    private EditText temp;
    private Lake newClosest;
    private ArrayList<String> counties;
    private ArrayList<LakeEntry> lakeList;
    public static final int FIND_ME_ACTIVITY_REQUEST = 156;

    /**
     * The standard onCreate functions called as well as creates the nav drawer and calls the init
     * function to set up the rest.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_trip_begin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        super.makeDrawer();

        init();

        Button findMeButton = (Button)findViewById(R.id.findMeButton);
        findMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager lm = (LocationManager)v.getContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}
                if(gps_enabled) {
                    Intent intent = new Intent(getBaseContext(), FindMeActivity.class);
                    startActivityForResult(intent, FIND_ME_ACTIVITY_REQUEST);
                }
                else{
                    Toast.makeText(LiveTripBegin.this, "Must have location turned on", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     *Counties Spinner is filled with values and given behavior
     *In case that Counties Spinner has no selected value, Lakes Spinner is made unusable
     */
    private void initializeList()
    {
        counties = fillCounties();
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.spinner_layout_ws, counties);
        adapt.setDropDownViewResource(R.layout.spinner_layout_ws);
        Spinner spin = (Spinner) findViewById(R.id.countySpinner);
        spin.setAdapter(adapt);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private Spinner lakes = (Spinner) findViewById(R.id.lakeSpinner);
            private TextView title = (TextView) findViewById(R.id.textViewCounty);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                lakes.setVisibility(Spinner.VISIBLE);
                title.setVisibility(TextView.VISIBLE);
                if(newClosest==null) {
                    ArrayList<LakeEntry> lakeList = fillLakes((String) parent.getItemAtPosition(position));
                    ArrayAdapter<LakeEntry> lakeAdapt = new ArrayAdapter<LakeEntry>(parent.getContext(), R.layout.spinner_layout_ws, lakeList);
                    lakes.setAdapter(lakeAdapt);
                }else{
                    newClosest = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lakes.setVisibility(Spinner.GONE);
                title.setVisibility(TextView.GONE);
            }
        });
    }

    /**
     * Counties read from FishAndLakes.db
     * @return ArrayList of Strings containing all county names
     */
    private ArrayList<String> fillCounties()
    {
        ArrayList<String> counties;
        counties = new ArrayList<String>();
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
            ex=  e;
            if(help != null)
            {
                help.close();
            }
        }
        return counties;
    }

    /**
     * Lakes read from FishAndLakes.db if they are in the correct county
     *
     * @param county County which lakes must be contained in to be added to list
     * @return Array of Strings containing all Lake names in given county
     */
    public ArrayList<LakeEntry> fillLakes(String county)
    {
        ArrayList<LakeEntry> lakeNames;
        lakeNames = new ArrayList<LakeEntry>();
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

    /**
     * Gets the start time of the trip and sets it to the trip object
     */
    public void beginTrip(){
        tripDate = new Date();
        trip.setStartDate(tripDate);
    }

    /*
    * Set behavior of extra features.
    *
    * Key extra features include weather and temperature
    */
    private void initializeExtras()
    {
        ArrayList<String> weathers = new ArrayList<String>();
        for(int i = 0; i < TripInfoStorage.WEATHER.length; i++)
            weathers.add(TripInfoStorage.WEATHER[i]);
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.spinner_layout_ws, weathers);
        adapt.setDropDownViewResource(R.layout.spinner_layout_ws);
        Spinner spin = (Spinner)findViewById(R.id.weatherCondSpinner);
        spin.setAdapter(adapt);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trip.setWeather((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                trip.setWeather(TripInfoStorage.WEATHER[TripInfoStorage.DEFAULT]);
            }
        });

        temp = (EditText) findViewById(R.id.temperatureEditText);

        temp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = temp.getText().toString();
                if (text.equals("")) {
                    trip.deleteTemperature();
                    temp.setHint("Fahrenheit");
                } else {
                    double t = Double.parseDouble(text);
                    trip.setTemperature(t);
                }
            }
        });
    }

    /**
     * Submit button behavior set
     *
     * On click Spinner values are used to query database and get all information on selected
     * lake. Lake object is then filled with these values and added to the current TripInfoStorage.
     * Date also added to TripInfoStorage.
     */
    private void initializeSubmit()
    {
        Button b = (Button)findViewById(R.id.startTripButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lake lake;
                try {
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext(), FISH_LAKES_DB);
                    db.openDatabase();
                    LakeEntry lakeEntry = (LakeEntry) ((Spinner) findViewById(R.id.lakeSpinner)).getSelectedItem();
                    String county = (String) ((Spinner) findViewById(R.id.countySpinner)).getSelectedItem();
                    String q = "SELECT _id,WaterBodyName,County,Abbreviation,Latitude,Longitude FROM Lakes WHERE _id=?";
                    SQLiteCursor cur = db.runQuery(q, new String[]{lakeEntry.id + ""});
                    cur.moveToFirst();
                    lake = new Lake(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3), cur.getDouble(4), cur.getDouble(5));
                    cur.close();
                    db.close();
                    trip.setLake(lake);
                    Intent intent = new Intent(v.getContext(), LiveTripMain.class);
                    intent.putExtra("TripInfo", trip);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast t = Toast.makeText(getApplicationContext(), "Database Read Error" + " " + e.toString() , Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });
    }

    /*
    Private class used to just store lake name and id

    id is used for database query after submit, so that correct lake is assured

    name is used for display in spinner
    */
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

    /**
     * Calls all necessary initialization for the beginning of the trip, and creates new TripInfoStorage
     */
    public void init(){
        trip = new TripInfoStorage();
        initializeList();
        initializeExtras();
        initializeSubmit();
        beginTrip();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            newClosest = (Lake)data.getSerializableExtra("ClosestLake");

            Spinner countySpinner = (Spinner)findViewById(R.id.countySpinner);

            int c = this.counties.indexOf(newClosest.getCounty());
            countySpinner.setSelection(c);

            setLakesSpinner();
        }
    }

    private void setLakesSpinner(){
        Spinner lakeSpinner = (Spinner) findViewById(R.id.lakeSpinner);

        lakeList = fillLakes(newClosest.getCounty());
        ArrayAdapter<LakeEntry> lakeAdapt = new ArrayAdapter<LakeEntry>(getApplicationContext(), R.layout.spinner_layout_ws, lakeList);
        lakeSpinner.setAdapter(lakeAdapt);

        String l = newClosest.getName();
        int p = -1;
        for(int e = 0; e < lakeList.size(); e ++){
            if(l.equals(lakeList.get(e).name))
                p = e;
        }
        lakeSpinner.setSelection(p);
    }
}
