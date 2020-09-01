package com.example.uparking3;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PieChartsParkingReservation extends MainMenu {

    private static String TAG = "PieChartsParkingReservation";

    int[] yData;
    String[] xData;
    private final String URL1 = "https://u-parking.000webhostapp.com/UParkingApp/busyparking.php";
    PieChart pieChart;
    BarChart BarChart;
    float[] yData1;
    int[] xData1;
    private final String URL2 = "https://u-parking.000webhostapp.com/UParkingApp/peekhour.php";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.parking_reservation_charts);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.parking_reservation_charts, null, false);
        Drawer.addView(contentView, 0);

        Log.d(TAG, "onCreate: starting to create chart");
        pieChart = (PieChart) findViewById(R.id.idPieChart);
        //
        Description description=new Description();
        description.setText("Busy Parking Lots");
        pieChart.setDescription(description);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(5);
        pieChart.setCenterText("Parking Lots Reservation");
        pieChart.setCenterTextSize(12);
        pieChart.setUsePercentValues(true);
        reservationOfParkingLot();
        Log.d(TAG, "onCreate: starting to create chart2 ");

        //peak hour
        BarChart = (BarChart) findViewById(R.id.idBarChart);
        Description description1=new Description();
        description1.setText("Peak Hours");
        BarChart.setDescription(description1);
        BarChart.setDrawValueAboveBar(true);
        BarChart.setContextClickable(true);
        BarChart.setFitBars(true);
        BarChart.setDrawValueAboveBar(true);
        parkingPeakHrs();


    }

    private void parkingPeakHrs() {
        Log.d(TAG, "PeakHoursRetrieve: Values retrieved.");
        RequestQueue requestQueue = Volley.newRequestQueue(PieChartsParkingReservation.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    //{"numbers":["3","2","11"],"parking_names":["Liwan ","Funland","Musadia Plaza 2"]}
                    JSONArray JArray=jsonObject.getJSONArray("peek_hour");
                    // Toast.makeText(PieChartsParkingReservation.this, response ,Toast.LENGTH_LONG).show();
                    yData1=new float[JArray.length()];
                    xData1=new int[JArray.length()];

                    for(int i=0; i<JArray.length(); i++) {
                        JSONObject jsonObj1=JArray.getJSONObject(i);
                        yData1[i]= jsonObj1.getInt("count");
                        xData1[i] = jsonObj1.getInt("hr");
                    }
                    addDataSet1(xData1, yData1);
                } catch (JSONException e) {

                    Toast.makeText(PieChartsParkingReservation.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });

        requestQueue.add(stringRequest);

    }

    private void reservationOfParkingLot() {

        Log.d(TAG, "BusyParkingRetrieve: Values retrieved.");
        RequestQueue requestQueue = Volley.newRequestQueue(PieChartsParkingReservation.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //{"numbers":["3","2","11"],"parking_names":["Liwan ","Funland","Musadia Plaza 2"]}
                    JSONArray JArray = jsonObject.getJSONArray("ParkingLot");
                    //Toast.makeText(PieChartsParkingReservation.this, sha256hex ,Toast.LENGTH_LONG).show();

                    xData = new String[JArray.length()];
                    yData = new int[JArray.length()];

                    for (int i = 0; i < JArray.length(); i++) {
                        JSONObject jsonObj1 = JArray.getJSONObject(i);
                        xData[i] = jsonObj1.getString("parking_name");
                        yData[i] = jsonObj1.getInt("p");
                    }
                    addDataSet(xData, yData);
                } catch (JSONException e) {

                    Toast.makeText(PieChartsParkingReservation.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });

        requestQueue.add(stringRequest);

    }

    private void addDataSet(String[] xData1, int[] yData1) {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        for (int i = 0; i < yData1.length; i++) {
            yEntrys.add(new PieEntry(yData1[i], xData1[i]));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Busy Parking Lots");
        pieDataSet.setSliceSpace(5);
        pieDataSet.setValueTextSize(24);
        //add colors to dataset
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.animateXY(2500, 2500);
        pieChart.invalidate();
    }

    private void addDataSet1(int[] xData1, float[] yData1) {
        Log.d(TAG, "addDataSet 2 started");
        ArrayList<BarEntry> yEntrys = new ArrayList<>();

        for (int i = 0; i < yData1.length; i++) {
            yEntrys.add(new BarEntry((float) xData1[i], yData1[i]));
        }
        ArrayList<String> xAxisLabel = new ArrayList<>();
        for (int i = 0; i < xData1.length; i++) {
            if (xData1[i] >= 12 && xData1[i] <= 23)
                xAxisLabel.add(xData1[i] + ":00PM");
            else if (xData1[i] >23 && xData1[i] <= 11)
                xAxisLabel.add(xData1[i] + ":00AM");
        }
        String[] labels = new String[xData1.length];
        for (int i = 0; i < xData1.length; i++) {
            if (xData1[i] >= 12 && xData1[i] <= 23)
                labels[i]= xData1[i] + ":00PM";
            else
                labels[i]= xData1[i] + ":00AM";
        }

        //create the data set
        BarDataSet barDataSet = new BarDataSet(yEntrys, "Peak Hours");
        barDataSet.setValueTextSize(12);
        //add colors to dataset
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //create pie data object
        BarData barData = new BarData( barDataSet);

        XAxis xAxis=BarChart.getXAxis();
        xAxis.setValueFormatter(new LabelFormatter(labels));
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        BarChart.setData(barData);
        //add legend to chart
        Legend legend = BarChart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        //legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        BarChart.animateXY(2500, 2500);
        //BarChart.setFitBars(true);
        BarChart.invalidate();
    }

    public class LabelFormatter extends ValueFormatter implements IAxisValueFormatter {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
    }



}