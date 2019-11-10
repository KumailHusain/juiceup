package com.example.juiceup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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



        final String strdata = getIntent().getStringExtra("jsondata");
        try {
            JSONObject data = new JSONObject(strdata);
            TextView name = findViewById(R.id.crowd_info_name);
            name.setText(data.getString("add"));
            JSONArray crowd = data.getJSONArray("crowd");
            JSONArray noise = data.getJSONArray("noise");

            BarChart chart = findViewById(R.id.crowd_info_crowdchart);
            BarChart chart2 = findViewById(R.id.crowd_info_noisechart);
            chart.setTouchEnabled(false);
            chart2.setTouchEnabled(false);
            YAxis yaxis = chart.getAxisLeft();
            yaxis.setAxisMaximum(4);
            yaxis.setDrawLabels(false);
            yaxis.setDrawGridLines(false);
            YAxis yaxis2 = chart2.getAxisLeft();
            yaxis2.setAxisMaximum(4);
            yaxis2.setDrawLabels(false);
            yaxis2.setDrawGridLines(false);

            yaxis = chart.getAxisRight();
            yaxis.setDrawGridLines(false);
            yaxis.setDrawLabels(false);
            yaxis2 = chart2.getAxisRight();
            yaxis2.setDrawGridLines(false);
            yaxis2.setDrawLabels(false);

            XAxis xaxis = chart.getXAxis();
            xaxis.setDrawGridLines(false);
            XAxis xaxis2 = chart2.getXAxis();
            xaxis2.setDrawGridLines(false);

            List<BarEntry> crowdentries = new ArrayList<>();
            List<BarEntry> noiseentries = new ArrayList<>();
            //Calendar rightNow = Calendar.getInstance();
            //int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            for(int i = 0; i < 24; i++) {
                crowdentries.add(new BarEntry(i, (float) crowd.getDouble(i)));
                noiseentries.add(new BarEntry(i, (float) noise.getDouble(i)));
            }
            BarDataSet crowdset = new BarDataSet(crowdentries, "Crowd Density");
            BarDataSet noiseset = new BarDataSet(noiseentries, "Noise Intensity");
            BarData crowddata = new BarData(crowdset);
            BarData noisedata = new BarData(noiseset);
            crowddata.setBarWidth(0.4f); // set custom bar width
            crowddata.setDrawValues(false);
            chart.setData(crowddata);
            chart.setFitBars(true); // make the x-axis fit exactly all bars
            chart.invalidate();
            noisedata.setBarWidth(0.4f); // set custom bar width
            noisedata.setDrawValues(false);
            chart2.setData(noisedata);
            chart2.setFitBars(true); // make the x-axis fit exactly all bars
            chart2.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject data = new JSONObject(strdata);
                    Uri gmmIntentUri = Uri.parse("google.streetview:cbll=4" +
                            data.getString("lat") +
                            "," +
                            data.getString("lon"));

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    startActivity(mapIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
