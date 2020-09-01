package com.example.uparking3;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.RequiresApi;


public class SearchPage extends MainMenu implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

        TextView DateEditText , FromTimeSpinner , ToTimeSpinner;
        Button apply,map;
        ImageView CelImage ;
        Spinner spinner  ;
        ArrayList<String> ParkingList;
        String ParkingName ;
        String username;
        SessionManager sessionManager;
        TimePickerDialog timePickerDialog;
        Calendar calendar;
        int currentHour;
        int currentMinute;
        String hour;



    String URL = "https://u-parking.000webhostapp.com/UParkingApp/parking.php";
    String Expierd_URL = "https://u-parking.000webhostapp.com/UParkingApp/CheckDate.php";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.searchpage);

            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contentView = inflater.inflate(R.layout.searchpage, null, false);
            Drawer.addView(contentView, 0);

            sessionManager = new SessionManager(this);
            sessionManager.checkLogin();


            CelImage = (ImageView) findViewById(R.id.cel_icon);
            CelImage.setOnClickListener(this);
            DateEditText = (TextView) findViewById(R.id.EditTextDate);
            DateEditText.setOnClickListener(this);
            apply = (Button) findViewById(R.id.apply);
            apply.setOnClickListener(this);
            map = (Button) findViewById(R.id.map);
            map.setOnClickListener(this);
            ParkingList = new ArrayList<>();
            spinner = (Spinner) findViewById(R.id.spinner);
            FromTimeSpinner = (TextView) findViewById(R.id.FromTimeSpinner);
            ToTimeSpinner = (TextView) findViewById(R.id.ToTimeSpinner);
            FromTimeSpinner.setOnClickListener(this);
            ToTimeSpinner.setOnClickListener(this);
            HashMap<String, String> user = sessionManager.getUserDetail();
            username = user.get(sessionManager.USERNAME);
            userMenu = username;
            LocalTime hours = ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS);
            hour = hours+"";

            //current date
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DateEditText.setText(date);

            //current time
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            FromTimeSpinner.setText(time);
            ToTimeSpinner.setText(time);

            load();

            Expierd_booking();

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ParkingName=  spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // DO Nothing here

                }
            });

        }



    public void load(){


        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("name");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("parking_name");
                        ParkingList.add(name);
                    }

                    spinner.setAdapter(new ArrayAdapter<String>(SearchPage.this, android.R.layout.simple_spinner_dropdown_item, ParkingList));
                }catch (JSONException e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);
    }


    public void Expierd_booking(){

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Expierd_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("time", hour);


                return params;
            }};

        requestQueue.add(stringRequest);


    }


    public void search_information(){


        int s= Integer.parseInt(FromTimeSpinner.getText().toString().substring(0,2));
        int e= Integer.parseInt(ToTimeSpinner.getText().toString().substring(0,2));
        int times=0 ;

        if(ParkingName == ""){
            Toast.makeText(SearchPage.this,"اختر موقف",Toast.LENGTH_SHORT).show();

        }


        else if((e<=s) ||  (s>=1 && s<=8 || e>=1 && e<=8 )){
            if( s>=1 && s<=8 || e>=1 && e<=8){

                Toast.makeText(SearchPage.this,"سيكون المكان مغلق بهذا الوقت ساعات العمل من 9 صباحا الى 11 مساءاً",Toast.LENGTH_LONG).show();

            }else{
            Toast.makeText(SearchPage.this," وقت خاطئ",Toast.LENGTH_LONG).show();
            }

        }

        else{

            for(int i=s ; i<e ;i++ ){

                times = times+1 ;

            }
            Intent in = new Intent(this ,parkingListPage_without_Slot.class);
            Bundle b = new Bundle();

            // in case we want transfer more of information we will repeat just the sentence below
            b.putString("ParkingName", ParkingName);
            b.putString("StartTime",  FromTimeSpinner.getText().toString());
            b.putString("EndTime",  ToTimeSpinner.getText().toString());
            b.putInt("times",  times);
            b.putString("user_ID",  username);
            b.putString("Date", DateEditText.getText().toString());
            in.putExtras(b);
            startActivity(in);
        }

        }





    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        month =month+1 ;
        String date = year + "-" + month + "-" + dayOfMonth;
        DateEditText.setText(date);

    }


    protected void map(){
        Intent in = new Intent(this ,MapsActivity.class);
        startActivity(in);

    }//Done



    @Override
        public void onClick(View v) {
            if (v == DateEditText || v==CelImage){
                DialogFragment date = new DatePickerFregment();
                date.show(getSupportFragmentManager(),"date Picker");

            }if (v == apply) {

            search_information();
        }
        if(v==map){

            Intent in = new Intent(this ,MapsActivity.class);
            Bundle b = new Bundle();
            b.putString("ParkingName", ParkingName);
            in.putExtras(b);
            startActivity(in);
        }
        if (v== FromTimeSpinner) {
            calendar = Calendar.getInstance();
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(SearchPage.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {


                    FromTimeSpinner.setText(String.format("%02d:%02d", hourOfDay, minutes));
                }
            }, currentHour, currentMinute, false);

            timePickerDialog.show();
        }


        if (v== ToTimeSpinner) {
            calendar = Calendar.getInstance();
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(SearchPage.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {

                    ToTimeSpinner.setText(String.format("%02d:%02d", hourOfDay, minutes)  );
                }
            }, currentHour, currentMinute, false);

            timePickerDialog.show();
        }

        }


    }
