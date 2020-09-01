package com.example.uparking3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class parkingListPage extends MainMenu implements View.OnClickListener {

    String URL = "https://u-parking.000webhostapp.com/UParkingApp/test.php";
    String Slot_URL = "https://u-parking.000webhostapp.com/UParkingApp/slot.php";

    String parkingName , Date , StartTime,EndTime;
    TextView textViewArray[] ;
    TextView Pname, time,date,slot ,ShowPrice;
    TableLayout table;
    TableRow row;
    ArrayList<Integer> busy_slot = new ArrayList<Integer>();
    Button check_out;
    int times ;
    int finalPrice ;
    String Username;
    HashMap<Integer, Integer> price = new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> slot_id = new HashMap<Integer, Integer>();
    int color;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.parkinglistpage);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.parkinglistpage, null, false);
        Drawer.addView(contentView, 0);


        Pname=(TextView) findViewById(R.id.parking_name);
        slot=(TextView) findViewById(R.id.slot_number);
        date=(TextView) findViewById(R.id.Date);
        time=(TextView) findViewById(R.id.Time);

        ShowPrice=(TextView) findViewById(R.id.Price);
        check_out=(Button) findViewById(R.id.check_out);
        check_out.setOnClickListener(this);


        Bundle  b = getIntent().getExtras();
        parkingName = b.getString("ParkingName");
        StartTime = b.getString("StartTime");
        EndTime = b.getString("EndTime");
        Date = b.getString("Date");
        times = b.getInt("times");
        Username = b.getString("user_ID");
        Pname.setText(parkingName+"");
        date.setText(Date+"");
        time.setText(" من الساعة "+StartTime+" الى "+EndTime);


        // get a reference for the TableLayout
        table = (TableLayout) findViewById(R.id.tableslot);

                    ListofSlot();




    }

    public void AvailableSlot(){
        final ProgressDialog loading = ProgressDialog.show(this, "يتم البحث عن المواقف", "الرجاء الانتظار .......", false, false);

        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Slot_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("info");
                    row = new TableRow(parkingListPage.this);
                    int mod = jsonArray.length()%7;
                    int len = jsonArray.length()+mod;
                    textViewArray = new TextView[len];
//                    t.setText(response);
                    for(int i=0; i<len; i++) {
                        textViewArray[i] = new TextView(parkingListPage.this);
                        textViewArray[i].setWidth(90);
                        textViewArray[i].setHeight(90);
                        textViewArray[i].setId(i);
                        if (i<jsonArray.length()){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        int slot_number=jsonObject1.getInt("slot_number");
                        slot_id.put(i, slot_number);
                        price.put(i, jsonObject1.getInt("price"));
                        if(busy_slot.contains(slot_number) ){
                            textViewArray[i].setBackground(getResources().getDrawable(R.drawable.red));
                            textViewArray[i].setOnClickListener(parkingListPage.this);


                        }
                        else{
                            textViewArray[i].setBackground(getResources().getDrawable(R.drawable.green));
                            textViewArray[i].setOnClickListener(parkingListPage.this);


                        }}
                        else{

                            textViewArray[i].setBackgroundColor(Color.WHITE);
                        }

                        row.addView(textViewArray[i]);

                        if((i+1)%7 == 0  ){
                            table.addView(row,new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            row = new TableRow(parkingListPage.this);

                        }


                    }





                }catch (JSONException e){

                   // Toast.makeText(parkingListPage.this, e.toString() ,Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("parking_name", parkingName);
                return params;
            }};

        requestQueue.add(stringRequest);
    }



    public void ListofSlot(){

        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("info");


                    for(int i=0; i<jsonArray.length(); i++) {

                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        int slot_number=jsonObject1.getInt("slot_number");
                        busy_slot.add(slot_number);

                    }


                    AvailableSlot();

                }catch (JSONException e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("parking_name", parkingName);
                params.put("start_time", StartTime);
                params.put("end_time", EndTime);
                params.put("date", Date);

                return params;
            }};

        requestQueue.add(stringRequest);
    }






    @Override
    public void onClick(View v) {

        if(textViewArray[color].getBackground().getConstantState()!= getResources().getDrawable(R.drawable.red).getConstantState()){
            textViewArray[color].setBackground(getResources().getDrawable(R.drawable.green));}

        if (v == check_out){
            if(ShowPrice.getText().toString().equals("")){
                Toast.makeText(parkingListPage.this,"الرجاء اختيار موقف" ,Toast.LENGTH_LONG).show();
            }
            else if(slot.getText().toString().equalsIgnoreCase("الموقف محجوز")){
                Toast.makeText(parkingListPage.this,"الرجاء اختيار موقف آخر ؛ هذا الموقف محجوز" ,Toast.LENGTH_LONG).show();

            }
            else {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
                alertDialogBuilderUserInput.setView(mView);


                final EditText plateNumber = (EditText) mView.findViewById(R.id.plate_number);
                final Button ok = (Button) mView.findViewById(R.id.ok);
                final Button cancel = (Button) mView.findViewById(R.id.cancel);
                final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
                alertDialog.show();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String plateNumber_pattern="^[A-Z]{3}-?[0-9]{3}$";

                        if(plateNumber.getText().toString().equals("")){
                            plateNumber.setError("ادخل لوحة السيارة ");

                        }else if (!plateNumber.getText().toString().matches(plateNumber_pattern)){

                            plateNumber.setError("يجب ان تحتوي لوحة السيارة على احرف كبيرة فقط وارقام ");

                        }else{
                            alertDialog.dismiss();
                            Intent in = new Intent(parkingListPage.this ,BookingConfirmation.class);
                            Bundle b = new Bundle();
                            // in case we want transfer more of information we will repeat just the sentence below
                            b.putString("ParkingName", parkingName);
                            b.putString("StartTime",  StartTime);
                            b.putString("EndTime",  EndTime);
                            b.putString("user_ID",  Username);
                            b.putString("Date", Date);
                            b.putString("plate_number", plateNumber.getText().toString());
                            b.putString("slot_number", slot.getText()+"");
                            b.putInt("price", finalPrice);
                            in.putExtras(b);
                            finish();
                            startActivity(in);

                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        // startActivity(new Intent (EditProfile.this , EditProfile.class));
                    }
                });


            }





        }
        else if(v.getBackground().getConstantState()== getResources().getDrawable(R.drawable.red).getConstantState()){
            slot.setText("الموقف محجوز");
            ShowPrice.setText("");


        }


        else{
                v.setBackground(getResources().getDrawable(R.drawable.gray));
                color = v.getId();
                int s= slot_id.get(v.getId());
                slot.setText(s+"");
                int p = price.get(v.getId());
                ShowPrice.setText(p * times + " رس ");
                finalPrice = p * times;

        }


    }
}
