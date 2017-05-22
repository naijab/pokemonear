package com.naijab.pokemonear.maps.pokemon;


import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class PokemonDataModel {

  String id;
  String name;
  String number;
  double latitude;
  double longitude;
  // TODO ควรใช้เป็น CamelCase ไปเลย ไม่ควรใช้ Snake Case
  // TODO ใช้ @Serializable ช่วย Map JSON Field เพื่อเปลี่ยนให้กลายเป็นชื่อตัวแปรที่ต้องการได้
  long expiration_timestamp;

  @ParcelConstructor
  public PokemonDataModel(String id, String name, String number, double latitude, double longitude,
      long expiration_timestamp) {
    this.id = id;
    this.name = name;
    this.number = number;
    this.latitude = latitude;
    this.longitude = longitude;
    this.expiration_timestamp = expiration_timestamp;
  }

  // TODO เพราะว่าไปตั้งชื่อตัวแปรเป็น Snake Case มันก็เลยกลายเป็น Method ที่มี _ แบบนี้
  public long getExpiration_timestamp() {
    return expiration_timestamp;
  }

  // TODO เพราะว่าไปตั้งชื่อตัวแปรเป็น Snake Case มันก็เลยกลายเป็น Method ที่มี _ แบบนี้
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

