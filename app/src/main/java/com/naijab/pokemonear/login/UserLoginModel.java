package com.naijab.pokemonear.login;

import org.parceler.Parcel;

@Parcel
public class UserLoginModel {

    String message;
    String token;

    public UserLoginModel(){ }

    public UserLoginModel(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
