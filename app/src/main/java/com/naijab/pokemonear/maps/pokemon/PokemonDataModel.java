package com.naijab.pokemonear.maps.pokemon;


import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class PokemonDataModel {

  String id;
  String name;
  String number;
  double latitude;
  double longitude;

  @SerializedName("expiration_timestamp")
  long expirationTimestamp;

  @ParcelConstructor
  public PokemonDataModel(String id, String name, String number, double latitude, double longitude,
      long expirationTimestamp) {
    this.id = id;
    this.name = name;
    this.number = number;
    this.latitude = latitude;
    this.longitude = longitude;
    this.expirationTimestamp = expirationTimestamp;
  }

  public long getExpirationTimestamp() {
    return expirationTimestamp;
  }

  public void setExpirationTimestamp(long expirationTimestamp) {
    this.expirationTimestamp = expirationTimestamp;
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

