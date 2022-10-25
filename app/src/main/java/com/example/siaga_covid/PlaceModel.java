package com.example.siaga_covid;

public class PlaceModel {
    private String placeName, time, placePicture;

    private PlaceModel(){}


    public PlaceModel(String placeName, String time, String placePicture) {
        this.placeName = placeName;
        this.time = time;
        this.placePicture = placePicture;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlacePicture() {
        return placePicture;
    }

    public void setPlacePicture(String placePicture) {
        this.placePicture = placePicture;
    }
}
