package com.example.uparking3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookingDetails extends MainMenu implements View.OnClickListener {


    String URL = "https://u-parking.000webhostapp.com/UParkingApp/booking_information.php";
    String Cancel_URL = "https://u-parking.000webhostapp.com/UParkingApp/Cancel_Booking.php";


    String BookingID;
    String parking_name ;
    String ID , Date;
    TextView name , date , time , price , plate , map;
    Button Cancel ;
    String start_time , plate_number;
    String booking_state ;
    String end_time;
    ImageView barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_details);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_detail, null, false);
        Drawer.addView(contentView, 0);
        Bundle  b = getIntent().getExtras();
        BookingID = b.getString("booking_id");

        if(BookingID.contains(" رقم الحجز : ")){
            ID= BookingID.substring(13);
        }
        else{

            ID= BookingID;
        }

        parking_name = b.getString("parking_name");
        booking_state = b.getString("booking_state");
        plate=(TextView) findViewById(R.id.plate_number);
        time=(TextView) findViewById(R.id.Time);
        price=(TextView) findViewById(R.id.Price);
        date=(TextView) findViewById(R.id.Date);
        name=(TextView) findViewById(R.id.parking_name);
        Cancel=(Button) findViewById(R.id.Cancel);
        map = (TextView) findViewById(R.id.map);
        barcode=(ImageView) findViewById(R.id.barcode);


        if(booking_state.equals("ملغي") || booking_state.equals("منتهي")){
            Cancel.setEnabled(false);
            Cancel.setBackgroundColor(Color.GRAY);
        }

        map.setOnClickListener(this);
        Cancel.setOnClickListener(this);


        detail();


    }



    public void detail(){

        final ProgressDialog loading = ProgressDialog.show(this, "", "الرجاء الانتظار .......", false, false);

        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("info");
                    for(int i=0; i<jsonArray.length(); i++) {

                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        Date=jsonObject1.getString("date");
                        start_time=jsonObject1.getString("start_time");
                        end_time=jsonObject1.getString("end_time");
                        plate_number=jsonObject1.getString("plate_number");

                        name.setText(parking_name);
                        date.setText(Date);
                        plate.setText(plate_number);
                        price.setText("10 رس ");
                        time.setText("من الساعة "+start_time.substring(0,5)+" الى الساعة  "+end_time.substring(0,5) );


                    }

                    if(booking_state.equals("ملغي") || booking_state.equals("منتهي")){
                        barcode.setImageResource(R.drawable.qrcode);
                    }else{


                    String text = ID+"*"+"Active"+"*"+Date+"*"+start_time+"*"+end_time;

                    MultiFormatWriter  multiFormatWriter = new MultiFormatWriter();
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.CODABAR, 500,500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        barcode.setImageBitmap(bitmap);


                    }
                    catch (WriterException e) {
                        e.printStackTrace();
                    }}


                }catch (JSONException e){
                    loading.dismiss();

                    Toast.makeText(BookingDetails.this, e.toString() ,Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();

            }

        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("booking_id", ID);

                return params;
            }};

        requestQueue.add(stringRequest);
    }



    @Override
    public void onClick(View v) {


        if(v == Cancel){

                AlertDialog.Builder AlertX = new AlertDialog.Builder(BookingDetails.this);
                AlertX.setTitle("الغاء الحجز");
                AlertX.setMessage("هل انت متاكد من الغاء حجزك ؟");
                AlertX.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cancel_Booking("Cancelled");
                    }
                });
                AlertX.setNegativeButton("لا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertX.show();
        }

        if(v==map){

            Intent in = new Intent(this ,MapsActivity.class);
            Bundle b = new Bundle();
            b.putString("ParkingName", parking_name);
            in.putExtras(b);
            startActivity(in);
        }
    }


    public void Cancel_Booking(final String state){
        final ProgressDialog loading = ProgressDialog.show(this, "", "الرجاء الانتظار .......", false, false);

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Cancel_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();

                if (state.equals("Cancelled"))
                Toast.makeText(getApplicationContext(),"تم الغاء حجزك بنجاح", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();

                Toast.makeText(getApplicationContext(),"نأسف تأكد من الشبكة", Toast.LENGTH_LONG).show();

            }

        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("booking_id", ID);
                params.put("booking_state", state);


                return params;
            }};

        requestQueue.add(stringRequest);
    }



}
