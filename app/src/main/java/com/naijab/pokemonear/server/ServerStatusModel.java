package com.naijab.pokemonear.server;

import org.parceler.Parcel;

// TODO ทำไมคลาสนี้ไม่อยู่ใน Package ที่ชื่อว่า network ล่ะ?
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
