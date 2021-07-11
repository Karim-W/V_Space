package com.sunflower.vspace;

import java.util.Map;

public class aUser {
    private String fName;
    private String lName;
    private String Email;
    private String Phone;
    private String imgURL;
    private Map<String, String> location;
    private String[] Circle;
    private String[] Events;

    public aUser(String f,String l,String p,String E){
        this.fName=f;
        this.lName=l;
        this.Phone= p;
        this.Email=E;
        this.Circle = null;
        this.Events = null;
        this.location = null;
        this.imgURL = "https://w7.pngwing.com/pngs/340/956/png-transparent-profile-user-icon-computer-icons-user-profile-head-ico-miscellaneous-black-desktop-wallpaper.png";
    }

    public String getfName() {
        return this.fName;
    }

    public String getlName() {
        return this.lName;
    }

    public String getEmail() {
        return this.Email;
    }

    public String getPhone() {
        return this.Phone;
    }

    public String getImgURL() {
        return this.imgURL;
    }

    public String[] getCircle() {
        return this.Circle;
    }

    public String[] getEvents() {
        return this.Events;
    }

    public Map<String, String> getLocation() {
        return this.location;
    }

    public void setfName(String f) {
        this.fName = f;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setCircle(String[] circle) {
        Circle = circle;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setEvents(String[] events) {
        Events = events;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
