package com.example.loginactivity;

/**
 * Created by User on 2/8/2017.
 */


public class UserInformation {

    private String name;
    private String email;
    private String usertype;

    public UserInformation(String name, String email, String userType ) {
        this.name = name;
        this.email = email;
        this.usertype = userType;


    }

    public UserInformation(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

}

