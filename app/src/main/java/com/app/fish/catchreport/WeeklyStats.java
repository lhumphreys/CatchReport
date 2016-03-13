package com.app.fish.catchreport;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WeeklyStats extends AppCompatActivity {

    String URL_WeeklyStats = "http://zoebaker.name/android_login_api/weeklyStats.php";
    private String lakeToGet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getExtras() != null)
        {
            String lakename = getIntent().getExtras().getString("Lake Name");
            lakeToGet = " for "+lakename;
        }
        else
        {
            lakeToGet = "";
        }

        TextView header = (TextView)findViewById(R.id.textView1);
        header.setText("Weekly Stats"+lakeToGet);

        final StringRequest strReq = new StringRequest(Request.Method.POST, URL_WeeklyStats, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        ArrayList<ArrayList<String>> allRows = new ArrayList<>();
                        JSONArray jsonArray = jObj.getJSONArray("result");

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            JSONArray iJsonArray = jsonArray.getJSONArray(i);
                            ArrayList<String> curRow = new ArrayList<>();

                            for(int j=0; j<iJsonArray.length(); j++)
                            {
                                curRow.add(iJsonArray.getString(j));
                            }

                            allRows.add(curRow);
                        }
                        String results = jObj.getString("result");

                        GraphView mygraph = (GraphView)findViewById(R.id.graph);
                        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                                new DataPoint(0,0),
                                new DataPoint(1,5),
                                new DataPoint(2,7),
                                new DataPoint(3,3),
                                new DataPoint(4,10),
                                new DataPoint(5,2),
                                new DataPoint(6,6),
                                new DataPoint(7,5),
                                new DataPoint(8,0)
                        });

                        mygraph.addSeries(series);
                        mygraph.setTitleTextSize(40);
                        series.setSpacing(50);
                        series.setDrawValuesOnTop(true);
                        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                            @Override
                            public int get(DataPoint data) {
                                return Color.parseColor("#E84D3C");
                            }
                        });

                        StaticLabelsFormatter slf = new StaticLabelsFormatter(mygraph);

                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK);

                        switch(day)
                        {
                            case Calendar.SUNDAY:
                                slf.setHorizontalLabels(new String[]{"","Mon","Tue","Wed","Thur","Fri","Sat","Sun",""});
                                break;
                            case Calendar.MONDAY:
                                slf.setHorizontalLabels(new String[]{"","Tue","Wed","Thur","Fri","Sat","Sun","Mon",""});
                                break;
                            case Calendar.TUESDAY:
                                slf.setHorizontalLabels(new String[]{"","Wed","Thur","Fri","Sat","Sun","Mon","Tue",""});
                                break;
                            case Calendar.WEDNESDAY:
                                slf.setHorizontalLabels(new String[]{"","Thur","Fri","Sat","Sun","Mon","Tue","Wed",""});
                                break;
                            case Calendar.THURSDAY:
                                slf.setHorizontalLabels(new String[]{"","Fri","Sat","Sun","Mon","Tue","Wed","Thur",""});
                                break;
                            case Calendar.FRIDAY:
                                slf.setHorizontalLabels(new String[]{"","Sat","Sun","Mon","Tue","Wed","Thur","Fri",""});
                                break;
                            case Calendar.SATURDAY:
                                slf.setHorizontalLabels(new String[]{"","Sun","Mon","Tue","Wed","Thur","Fri","Sat",""});
                                break;
                        }

                        mygraph.getGridLabelRenderer().setLabelFormatter(slf);


                    } else {
                        // Error. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not display weekly stats!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(),"Something went wrong: " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }})
        {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("lake", lakeToGet);
                return params;
            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(strReq);
    }

}
