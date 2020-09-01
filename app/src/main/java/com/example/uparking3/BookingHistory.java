package com.example.uparking3;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class BookingHistory extends TabActivity {



        private static final String UPCOMING = "الحجوزات القادمة";
        private static final String OUTGOING = "الحجوزات المنتهية";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            TabHost tabHost = getTabHost();


            TabHost.TabSpec UPcoming = tabHost.newTabSpec(UPCOMING);
            UPcoming.setIndicator(UPCOMING);
            Intent UPcomingIntent = new Intent(this, Booking_List.class);
            Bundle b = new Bundle();
            b.putString("Booking_state", UPCOMING);
            UPcomingIntent.putExtras(b);
            UPcoming.setContent(UPcomingIntent);



            TabHost.TabSpec Outgoing = tabHost.newTabSpec(OUTGOING);
            Outgoing.setIndicator(OUTGOING);
            Intent OutgoingIntent = new Intent(this, Booking_List.class);
            Bundle b1 = new Bundle();
            b1.putString("Booking_state", OUTGOING);
            OutgoingIntent.putExtras(b1);
            Outgoing.setContent(OutgoingIntent);


            tabHost.addTab(UPcoming); // Adding Inbox tab
            tabHost.addTab(Outgoing); // Adding Outbox tab
        }




}
