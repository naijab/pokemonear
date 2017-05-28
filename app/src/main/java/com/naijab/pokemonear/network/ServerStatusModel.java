package com.naijab.pokemonear.network;

import org.parceler.Parcel;

@Parcel
public class ServerStatusModel {

    String message;

    public ServerStatusModel(){}

    public ServerStatusModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
