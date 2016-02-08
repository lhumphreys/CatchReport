package com.app.fish.catchreport;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class WeeklyStats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GraphView mygraph = (GraphView)findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0,0),
                new DataPoint(1,5),
                new DataPoint(2,7),
                new DataPoint(3,3),
                new DataPoint(4,10),
                new DataPoint(5,2),
                new DataPoint(6,0)
        });
        mygraph.addSeries(series);
        mygraph.setTitle("Weekly Catch Report");
        mygraph.setTitleTextSize(40);
        series.setSpacing(50);
        series.setDrawValuesOnTop(true);

        StaticLabelsFormatter slf = new StaticLabelsFormatter(mygraph);
        slf.setHorizontalLabels(new String[]{"","Mon","Tue","Wed","Thur","Fri",""});
        mygraph.getGridLabelRenderer().setLabelFormatter(slf);

    }

}
