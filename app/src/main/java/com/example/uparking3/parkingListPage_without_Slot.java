package com.example.uparking3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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


public class parkingListPage_without_Slot extends MainMenu implements View.OnClickListener {

    String URL = "https://u-parking.000webhostapp.com/UParkingApp/test.php";
    String Slot_URL = "https://u-parking.000webhostapp.com/UParkingApp/NumberOfSlots.php";

    String parkingName , Date , StartTime,EndTime;
    TextView Pname, time,date,slot ,ShowPrice;


    ArrayList<Integer> busy_slot = new ArrayList<Integer>();
    Button check_out;
    int times ;
    int finalPrice ;
    String Username;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.parkinglistpage);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.parkinglistpage1, null, false);
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
        ShowPrice.setText("10 رس");




        ListofSlot();




    }

    public void AvailableSlot(){
        final ProgressDialog loading = ProgressDialog.show(this, "يتم البحث عن المواقف", "الرجاء الانتظار .......", false, false);

        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest=new StringRequest(Request.Method.POST, Slot_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                int NumberOfSlot =  Integer.parseInt(response) - 1 ;

                if (busy_slot.size()== NumberOfSlot){
                    AlertDialog.Builder builder = new AlertDialog.Builder(parkingListPage_without_Slot.this);
                    builder.setTitle("تنبيه !!");
                    builder.setMessage("نأسف الموقف ممتلئ هذا الوقت");
                    // add a button
                    builder.setPositiveButton("حسناً", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent in = new Intent(parkingListPage_without_Slot.this ,SearchPage.class);
                            finish();
                            startActivity(in);

                        }
                    });
                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }else {
                    slot.setText(busy_slot.size()+1+""); ;
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

        if (v == check_out){
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
                        String plateNumber_pattern="^[A-Z]{3}-?[0-9]{1,4}$";

                        if(plateNumber.getText().toString().equals("")){
                            plateNumber.setError("ادخل لوحة السيارة ");

                        }else if (!plateNumber.getText().toString().matches(plateNumber_pattern)){

                            plateNumber.setError("يجب ان تحتوي لوحة السيارة على احرف كبيرة فقط وارقام ");

                        }else{
                            alertDialog.dismiss();
                            Intent in = new Intent(parkingListPage_without_Slot.this ,BookingConfirmation.class);
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






        }}



    }

