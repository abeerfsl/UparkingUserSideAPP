package com.example.uparking3;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.List;
import java.util.Map;

public class Booking_List extends MainMenu {

    String URL = "https://u-parking.000webhostapp.com/UParkingApp/list.php";
    String History_URL = "https://u-parking.000webhostapp.com/UParkingApp/ShowInHistory.php";


    public static final Integer images = R.drawable.logo;
    ListView listView;
    List listRowItems;
    String username ;

    CustomListViewAdapter customListViewAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.booking_list);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.booking_list, null, false);
        Drawer.addView(contentView, 0);

        listView = (ListView) findViewById(R.id.list);

        Bundle  b = getIntent().getExtras();
        String Booking_state  = b.getString("Booking_state");

        if (Booking_state.equals("الحجوزات القادمة")){
            load("Active");

        }
        if (Booking_state.equals("الحجوزات المنتهية")){
            load("DeActive");
        }


        username =  getIntent().getStringExtra("user_ID");




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customListViewAdapter.getView(position, view, parent);
                String booking_id = customListViewAdapter.BookingID.getText().toString();
                String parking_name = customListViewAdapter.ParkingName.getText().toString();
                String booking_state = customListViewAdapter.BookingState.getText().toString();

                goToDetails(booking_id,parking_name,booking_state);




            }});




        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " تحديد");
                // Calls toggleSelection method from ListViewAdapter Class
                customListViewAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.delete_menu_option, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete:
                        AlertDialog.Builder AlertX= new AlertDialog.Builder(Booking_List.this);
                        AlertX.setTitle("تأكيد الحذف ");
                        AlertX.setMessage("هل انت متأكد ؟");
                        AlertX.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray selected = customListViewAdapter.getSelectedIds();

                                // Captures all selected ids with a loop
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        ListRowModel selectedListItem = (ListRowModel) customListViewAdapter.getItem(selected.keyAt(i));
                                        int booking_id = selectedListItem.getBookingID();
                                        show_history(booking_id);
                                        // Remove selected items using ids
                                        customListViewAdapter.remove(selectedListItem);
                                    }
                                }
                                mode.finish();
                            }
                        });
                        AlertX.setNegativeButton("لا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertX.show();
                        return true;
                    default:
                        return false;
                    // call getSelectedIds method from customListViewAdapter

                }



            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                customListViewAdapter.removeSelection();
            }
        });

    }



    public void goToDetails(String id,String name,String booking_state){

        Intent in = new Intent(this ,BookingDetails.class);
        Bundle b = new Bundle();
        b.putString("booking_id",id);
        b.putString("parking_name",name);
        b.putString("booking_state",booking_state);
        in.putExtras(b);
        startActivity(in);

    }




    public void load(final String Check_State) {
        final ProgressDialog loading = ProgressDialog.show(this, "", "الرجاء الانتظار .......", false, false);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    listRowItems = new ArrayList();
                    JSONArray jsonArray = jsonObject.getJSONArray("info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.getString("parking_name");
                        String State = jsonObject1.getString("booking_state");
                        int booking_id = jsonObject1.getInt("id");

                        if (Check_State.equals("Active")) {

                            if (State.equals("Active")) {
                                State = "مفعل";
                                ListRowModel item = new ListRowModel(images, name, State, booking_id);
                                listRowItems.add(item);
                            }
                        }else {

                            if (State.equals("Cancelled")) {
                                State = "ملغي";
                                ListRowModel item = new ListRowModel(images, name, State, booking_id);
                                listRowItems.add(item);
                            }

                            if (State.equals("Extended")) {
                                State = "تم تمديد الوقت";
                                ListRowModel item = new ListRowModel(images, name, State, booking_id);
                                listRowItems.add(item);
                            }

                            if (State.equals("Expired")) {
                                State = "منتهي";
                                ListRowModel item = new ListRowModel(images, name, State, booking_id);
                                listRowItems.add(item);
                            }
                        }


                    }
                    customListViewAdapter = new CustomListViewAdapter(Booking_List.this, R.layout.list_item_row, listRowItems);
                    listView.setAdapter(customListViewAdapter);

                } catch (JSONException e) {
                    Toast.makeText(Booking_List.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Toast.makeText(Booking_List.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", "Sah");
                return params;
            }};

        requestQueue.add(stringRequest);



    }

    public void show_history(final int booking_id) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, History_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Booking_List.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Toast.makeText(Booking_List.this, "نأسف تأكد من الشبكة", Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("booking_id", booking_id+"");
                return params;
            }};

        requestQueue.add(stringRequest);



    }

}
