package com.example.uparking3;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng Redsea_Mall;
    LatLng Haifaa_Mall ;
    LatLng Makkah_Mall ;
    LatLng AlSalam_Mall;
    LatLng Aziziyah_Mall;
    String parkingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle  b = getIntent().getExtras();
        parkingName = b.getString("ParkingName");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        Redsea_Mall = new LatLng(21.627669, 39.110965);
        Haifaa_Mall = new LatLng(21.527381, 39.177224);
        Makkah_Mall = new LatLng(21.391091, 39.884621);
        AlSalam_Mall = new LatLng(21.507845, 39.223226);
        Aziziyah_Mall = new LatLng(21.405170, 39.871708);


        switch (parkingName){

            case "موقف 1" :
                mMap.addMarker(new MarkerOptions().position(Redsea_Mall).title("موقف 1").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Redsea_Mall,20));
                break;
            case "موقف 2" :
                mMap.addMarker(new MarkerOptions().position(Haifaa_Mall).title("موقف 2").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))).showInfoWindow();;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Haifaa_Mall,20));
                break;
            case "موقف 3" :
                mMap.addMarker(new MarkerOptions().position(Makkah_Mall).title("موقف 3").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))).showInfoWindow();;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Makkah_Mall,20));
                break;
            case "موقف 4" :
                mMap.addMarker(new MarkerOptions().position(AlSalam_Mall).title("موقف 4").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))).showInfoWindow();;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AlSalam_Mall,20));
                break;
            case "موقف 5" :
                mMap.addMarker(new MarkerOptions().position(Aziziyah_Mall).title("موقف 5").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))).showInfoWindow();;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Aziziyah_Mall,20));
                break;

            default:
                mMap.addMarker(new MarkerOptions().position(Redsea_Mall).title("موقف 1").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                mMap.addMarker(new MarkerOptions().position(Haifaa_Mall).title("موقف 2").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                mMap.addMarker(new MarkerOptions().position(Makkah_Mall).title("موقف 3").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Makkah_Mall,20));
                mMap.addMarker(new MarkerOptions().position(AlSalam_Mall).title("موقف 4").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                mMap.addMarker(new MarkerOptions().position(Aziziyah_Mall).title("موقف 5").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));


        }

    }

}

