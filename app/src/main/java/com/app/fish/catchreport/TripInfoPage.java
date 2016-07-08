package com.app.fish.catchreport;

import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Trevor Sherwood
 * @version 1.0
 *
 * Page used for 'Start Trip' feature. Stores TripInfoStorage object.
 */
public class TripInfoPage extends BaseDrawerActivity {


    public static final String FISH_LAKES_DB = "FishAndLakes.db";

    private ArrayList<LakeEntry> lakeList;
    private ArrayList<String> counties;
    private TripInfoStorage info;
    private EditText temp;
    private String currentCounty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info_page);
        super.makeDrawer();


        info = new TripInfoStorage();
        info.setStartDate(new Date());
        info.setEndDate(new Date());

        initializeList();
        initializeCalendar();
        initializeClock();
        initializeExtras();
        initializeSubmit();


    }

    /**
     * Counties Spinner is filled with values and given behavior
     *
     * In case that Counties Spinner has no selected value, Lakes Spinner is made unusable
     */
    private void initializeList()
    {

        counties = fillCounties();
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.spinner_layout_ws, counties);
        adapt.setDropDownViewResource(R.layout.spinner_layout_ws);
        Spinner spin = (Spinner)findViewById(R.id.countySpinner);
        spin.setAdapter(adapt);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private Spinner lakes = (Spinner) findViewById(R.id.lakeSpinner);
            private TextView title = (TextView) findViewById(R.id.textViewCounty);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                lakes.setVisibility(Spinner.VISIBLE);
                title.setVisibility(TextView.VISIBLE);
                lakeList = fillLakes((String) parent.getItemAtPosition(position));
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



    /**
     * Counties read from FishAndLakes.db
     * @return ArrayList of Strings containing all county names
     */
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

    /**
     * CalendarView current date is read and Date in info is set
     */
    private void initializeCalendar()
    {
        //CalendarView cv = (CalendarView)findViewById(R.id.calendarView);
        DatePicker dp = (DatePicker)findViewById(R.id.datePicker);
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        dp.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
                info.getStartDate().setYear(year-1900);
                info.getStartDate().setMonth(month);
                info.getStartDate().setDate(dayOfMonth);
                info.getEndDate().setYear(year);
                info.getEndDate().setMonth(month);
                info.getEndDate().setDate(dayOfMonth);
            }
        });
    }

    /**
     * TimePicker current time is read and Date in info is set
     */
    private void initializeClock()
    {
        TimePicker tp = (TimePicker)findViewById(R.id.timePicker1);
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                info.getStartDate().setHours(hourOfDay);
                info.getStartDate().setMinutes(minute);
            }
        });
        TimePicker tp2 = (TimePicker)findViewById(R.id.timePicker2);
        tp2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                info.getEndDate().setHours(hourOfDay);
                info.getEndDate().setMinutes(minute);
            }
        });
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
        Spinner spin = (Spinner)findViewById(R.id.weatherSpinner);
        spin.setAdapter(adapt);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                info.setWeather((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                info.setWeather(TripInfoStorage.WEATHER[TripInfoStorage.DEFAULT]);
            }
        });

        temp = (EditText) findViewById(R.id.tempEditText);

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
                if(text.equals("")) {
                    info.deleteTemperature();
                    temp.setHint("Fahrenheit");
                }
                else {
                    double t = Double.parseDouble(text);
                    info.setTemperature(t);
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
        Button b = (Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View myview = v;
                Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.press);
                v.startAnimation(anim);

                /** new intent will be called on animation completion**/
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        Lake lake;
                        try {
                            int hDif = info.getEndDate().getHours() - info.getStartDate().getHours();
                            int mDif = info.getEndDate().getMinutes() - info.getStartDate().getMinutes();
                            if(mDif < 0){
                                mDif += 60;
                                hDif -= 1;
                            }
                            if(hDif >= 0) {
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
                                info.setLake(lake);
                                Intent intent = new Intent(myview.getContext(), AddFishActivity.class);
                                intent.putExtra("TripInfo", info);
                                startActivity(intent);
                            }
                            else
                                Toast.makeText(TripInfoPage.this, "Start time must be before end time", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast t = Toast.makeText(getApplicationContext(), "Database Read Error", Toast.LENGTH_LONG);
                            t.show();
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
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

}
