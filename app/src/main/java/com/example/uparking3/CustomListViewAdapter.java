package com.example.uparking3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomListViewAdapter extends ArrayAdapter {
    Context context;
    private SparseBooleanArray selectedListItemsIds;
    List multipleSelectionList;
    ImageView img;
    TextView ParkingName;
    TextView BookingState;
    TextView BookingID;


    public CustomListViewAdapter(Context context, int resourceId, List items) {
        super(context, resourceId, items);
        this.context = context;
        selectedListItemsIds = new SparseBooleanArray();
        this.multipleSelectionList = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListRowModel rowItem = (ListRowModel) getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.list_item_row, null);
            BookingID = (TextView) convertView.findViewById(R.id.BookingID);
            ParkingName = (TextView) convertView.findViewById(R.id.ParkingName);
            BookingState = (TextView) convertView.findViewById(R.id.BookingState);
            img = (ImageView) convertView.findViewById(R.id.img);


        if(rowItem.getBookingState().equalsIgnoreCase("مفعل")){
            BookingState.setText(rowItem.getBookingState());
            BookingState.setTextColor(Color.parseColor("#01A838"));
        }

        else {
            BookingState.setText(rowItem.getBookingState());
            BookingState.setTextColor(Color.RED);

        }

        BookingID.setText(" رقم الحجز : "+rowItem.getBookingID());
        ParkingName.setText(rowItem.getParkingName());
        img.setImageResource(rowItem.getImage());

        return convertView;
    }

    public void remove(ListRowModel object) {
        multipleSelectionList.remove(object);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedListItemsIds.get(position));
    }

    public void removeSelection() {
        selectedListItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            selectedListItemsIds.put(position, value);
        else
            selectedListItemsIds.delete(position);
        notifyDataSetChanged();
    }


    public SparseBooleanArray getSelectedIds() {
        return selectedListItemsIds;
    }
}