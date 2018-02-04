package com.example.amr.onair.models;

import java.io.Serializable;

/**
 * Created by amr on 01/02/18.
 */

public class Client implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String nationalID;
    private String imagreURL;
    private boolean isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getImagreURL() {
        return imagreURL;
    }

    public void setImagreURL(String imagreURL) {
        this.imagreURL = imagreURL;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
