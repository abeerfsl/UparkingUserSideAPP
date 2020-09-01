package com.example.uparking3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    public DrawerLayout Drawer;
    public ActionBarDrawerToggle Toggle;
    String userMenu ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        Drawer = (DrawerLayout)findViewById(R.id.nav);


        Toggle = new ActionBarDrawerToggle(this, Drawer,R.string.open, R.string.close);
        Drawer.addDrawerListener(Toggle);
        Toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(Toggle.onOptionsItemSelected(item))

        return true;

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.BookingHistory) {
            SessionManager sessionManager = new SessionManager(this);
            userMenu= sessionManager.getUsername();
            Intent in = new Intent(this ,BookingHistory.class);
            in.putExtra("user_ID",userMenu);
            startActivity(in);


        } else if (id == R.id.home) {
            SessionManager sessionManager = new SessionManager(this);
            userMenu= sessionManager.getUsername();
            Intent in = new Intent(this ,SearchPage.class);
            in.putExtra("user_ID",userMenu);
            startActivity(in);
            finish();

        } else if (id == R.id.logout) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logout();


        } else if (id == R.id.EditProfile) {
            SessionManager sessionManager = new SessionManager(this);
            userMenu= sessionManager.getUsername();
            Intent in = new Intent(this ,EditProfile.class);
            in.putExtra("user_ID",userMenu);
            startActivity(in);

        }

        else if (id == R.id.parkingReservation) {
            SessionManager sessionManager = new SessionManager(this);
            userMenu= sessionManager.getUsername();
            Intent in = new Intent(this ,PieChartsParkingReservation.class);
            in.putExtra("user_ID",userMenu);
            startActivity(in);

        }
        /*else if (id == R.id.help) {
            Intent in = new Intent(this ,help.class);
            in.putExtra("user_ID",user);
            startActivity(in);
            finish();
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
