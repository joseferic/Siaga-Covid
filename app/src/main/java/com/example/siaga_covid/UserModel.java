package com.example.siaga_covid;

public class UserModel {
    private String userName, userTime, userPicture,Device;

    private UserModel(){}

    public UserModel(String userName, String userTime, String userPicture, String Device, String device) {
        this.userName = userName;
        this.userTime = userTime;
        this.userPicture = userPicture;
        this.Device = device;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTime() {
        return userTime;
    }

    public void setUserTime(String userTime) {
        this.userTime = userTime;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }
}
