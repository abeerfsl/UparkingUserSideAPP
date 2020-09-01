package com.example.uparking3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);


        Intent a = new Intent(getApplicationContext(), SearchPage.class);
        startActivity(a);
    }
 }

