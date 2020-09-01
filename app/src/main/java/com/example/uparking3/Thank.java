package com.example.uparking3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class Thank extends AppCompatActivity implements View.OnClickListener {


    Button booking_details, done;
    String BookingID, parking_name, booking_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank);

        booking_details = (Button) findViewById(R.id.booking_inf);

        done = (Button) findViewById(R.id.done);

        Bundle  b = getIntent().getExtras();
        BookingID = b.getString("booking_id");
        parking_name = b.getString("parking_name");
        booking_state = b.getString("booking_state");

        booking_details.setOnClickListener(this);
        done.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if (v == booking_details){

            Intent in = new Intent(this ,BookingDetails.class);
            Bundle b = new Bundle();
            b.putString("booking_id",BookingID);
            b.putString("parking_name",parking_name);
            b.putString("booking_state","مفعل");
            in.putExtras(b);
            finish();
            startActivity(in);

        }

        if(v==done){

            Intent in = new Intent(this ,SearchPage.class);

            finish();
            startActivity(in);
        }

    }
}
