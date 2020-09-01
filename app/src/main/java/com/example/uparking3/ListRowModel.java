package com.example.uparking3;

public class ListRowModel {
    private int image;
    private String ParkingName;
    private String BookingState;
    private int BookingID;

    public ListRowModel(int image, String ParkingName, String BookingState, int BookingID) {
        this.image = image;
        this.ParkingName = ParkingName;
        this.BookingState=BookingState;
        this.BookingID = BookingID;

    }
    public int getImage() {
        return image;
    }
    public void setImage(int image) {
        this.image = image;
    }
    public int getBookingID() {
        return BookingID;
    }
    public void setParkingID(int BookingID) {
        this.BookingID = BookingID;
    }
    public String getBookingState() {
        return BookingState;
    }
    public void setBookingState(String BookingState) {
        this.BookingState = BookingState;
    }

    public String getParkingName() {
        return ParkingName;
    }
    public void setParkingName(String ParkingName) {
        this.ParkingName = ParkingName;
    }
    @Override
    public String toString() {
        return ParkingName +"\n" + BookingID;
    }
}