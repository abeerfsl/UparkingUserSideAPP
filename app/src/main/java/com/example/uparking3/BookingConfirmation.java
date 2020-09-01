package com.example.uparking3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class BookingConfirmation extends MainMenu implements View.OnClickListener {


    Button confirm;
    String insert_URL = "https://u-parking.000webhostapp.com/UParkingApp/insert_booking.php";
    private TextView Amount;
    TextView Pname, time,date, plate;
    String parkingName , Date , StartTime,EndTime,Username ,slot_number ,plate_number;

    int price ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.booking_confirmation);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.booking_confirmation, null, false);
        Drawer.addView(contentView, 0);

        Pname=(TextView) findViewById(R.id.parking_name);
        plate=(TextView) findViewById(R.id.plate_number);
        date=(TextView) findViewById(R.id.Date);
        time=(TextView) findViewById(R.id.Time);
        Amount = (TextView) findViewById(R.id.Price);
        confirm = (Button) findViewById(R.id.check_out);
        confirm.setOnClickListener(this);

        Bundle  b = getIntent().getExtras();
        parkingName = b.getString("ParkingName");
        StartTime = b.getString("StartTime");
        EndTime = b.getString("EndTime");
        Date = b.getString("Date");
        Username = b.getString("user_ID");
        slot_number=b.getString("slot_number");
        price=b.getInt("price");
        plate_number=b.getString("plate_number");

        Pname.setText(parkingName+"");
        date.setText(Date+"");
        time.setText(" من الساعة "+StartTime+" الى "+EndTime);
        plate.setText(plate_number);
        Amount.setText(price+" رس ");


    }

    // i attached my CV ,i as you disscused with shroug ,

    public void insert_booking(){

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, insert_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent in = new Intent(BookingConfirmation.this ,Thank.class);
                Bundle b = new Bundle();
                b.putString("booking_id",response);
                b.putString("parking_name",parkingName);
                b.putString("booking_state","Active");
                in.putExtras(b);
                finish();
                startActivity(in);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();

            }

        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("parking_name", parkingName);
                params.put("start_time", StartTime);
                params.put("end_time", EndTime);
                params.put("date", Date);
                params.put("user_id", "sah");
                params.put("slot_number", slot_number);
                params.put("plate_number", plate_number);


                return params;
            }};

        requestQueue.add(stringRequest);


    }

    @Override
    public void onClick(View v) {

        if(v == confirm){
            insert_booking();



        }
    }
}
