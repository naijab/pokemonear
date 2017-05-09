package com.naijab.pokemonear.login.user;

import org.parceler.Parcel;

@Parcel
public class UserLoginModel {

  String message;
  String token;

  public UserLoginModel() {
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
