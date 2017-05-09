package com.naijab.pokemonear.maps.pokemon;

import java.util.List;
import org.parceler.Parcel;

@Parcel
public class PokemonCatchableModel {

  String message;
  List<PokemonDataModel> data;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<PokemonDataModel> getData() {
    return data;
  }

  public void setData(List<PokemonDataModel> data) {
    this.data = data;
  }

}
