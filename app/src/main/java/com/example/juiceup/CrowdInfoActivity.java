package com.example.juiceup;

import android.app.Activity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CrowdInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_info2);



        String strdata = getIntent().getStringExtra("jsondata");
        try {
            JSONObject data = new JSONObject(strdata);
            TextView name = findViewById(R.id.crowd_info_name);
            name.setText(data.getString("add"));
            JSONArray crowd = data.getJSONArray("crowd");
            JSONArray noise = data.getJSONArray("noise");

            BarChart chart = findViewById(R.id.crowd_info_crowdchart);
            chart.setTouchEnabled(false);
            YAxis yaxis = chart.getAxisLeft();
            yaxis.setAxisMaximum(4);
            yaxis.setDrawGridLines(false);

            yaxis = chart.getAxisRight();
            yaxis.setDrawGridLines(false);
            yaxis.setDrawLabels(false);

            XAxis xaxis = chart.getXAxis();
            xaxis.setDrawGridLines(false);

            List<BarEntry> crowdentries = new ArrayList<>();
            List<BarEntry> noiseentries = new ArrayList<>();
            Calendar rightNow = Calendar.getInstance();
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            for (int i = currentHour; i < currentHour + 5; i++) {
                crowdentries.add(new BarEntry(i, (float) crowd.getDouble(i)));
                noiseentries.add(new BarEntry(i, (float) noise.getDouble(i)));
            }
            BarDataSet crowdset = new BarDataSet(crowdentries, "BarDataSet");
            BarDataSet noiseset = new BarDataSet(noiseentries, "BarDataSet");
            BarData crowddata = new BarData(crowdset);
            crowddata.setBarWidth(0.9f); // set custom bar width
            crowddata.setDrawValues(false);
            chart.setData(crowddata);
            chart.setFitBars(true); // make the x-axis fit exactly all bars
            chart.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
    }

}
