package com.example.travelplan;

public class TravelPlan {
    private int id;
    private String place;
    private String day;
    private String time;
    private String address;
    private byte[] image;

    public TravelPlan(String place, String day, String time, String address, byte[] image, int id) {
        this.place = place;
        this.day = day;
        this.time = time;
        this.address = address;
        this.image = image;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
