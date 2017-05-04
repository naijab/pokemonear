package com.naijab.pokemonear.maps;


import org.parceler.Parcel;

@Parcel
public class PokemonDataModel {

    long expiration_timestamp;
    double latitude;
    double longitude;
    String name;
    String number;
    String id;

    public long getExpiration_timestamp() {
        return expiration_timestamp;
    }

    public void setExpiration_timestamp(long expiration_timestamp) {
        this.expiration_timestamp = expiration_timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

