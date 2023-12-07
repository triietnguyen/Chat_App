package com.example.demomvvm.Model;

import java.io.Serializable;

public class User implements Serializable {

    public String name, image, email, token, id;

    public User(){

    }

    public User(String name, String image, String email, String token) {
        this.name = name;
        this.image = image;
        this.email = email;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
